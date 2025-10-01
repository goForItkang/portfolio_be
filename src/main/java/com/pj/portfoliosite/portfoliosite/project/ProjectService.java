package com.pj.portfoliosite.portfoliosite.project;

import com.pj.portfoliosite.portfoliosite.global.dto.*;
import com.pj.portfoliosite.portfoliosite.global.entity.Project;
import com.pj.portfoliosite.portfoliosite.global.entity.ProjectBookMark;
import com.pj.portfoliosite.portfoliosite.global.entity.ProjectComment;
import com.pj.portfoliosite.portfoliosite.global.entity.User;
import com.pj.portfoliosite.portfoliosite.project.bookmark.ProjectBookMarkRepository;
import com.pj.portfoliosite.portfoliosite.project.comment.ProjectCommentRepository;
import com.pj.portfoliosite.portfoliosite.project.like.ProjectLikeRepository;
import com.pj.portfoliosite.portfoliosite.project.like.ProjectLikeService;
import com.pj.portfoliosite.portfoliosite.user.UserRepository;
import com.pj.portfoliosite.portfoliosite.user.UserService;
import com.pj.portfoliosite.portfoliosite.util.AESUtil;
import com.pj.portfoliosite.portfoliosite.util.ImgUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserService userService; // userServiced;
    private final UserRepository userRepository;
    private final ImgUtil imageUtil;
    private final ImgUtil imgUtil;
    private final ProjectCommentRepository projectCommentRepository;
    private final ProjectLikeService projectLikeService;
    private final ProjectLikeRepository projectLikeRepository;
    private final ProjectBookMarkRepository projectBookMarkRepository;
    private final AESUtil aesUtil;

    //추천 프로젝트 로직 오늘 부터 일주일 동안 가장 많은 좋아요 갯수
    public List<ResProjectRecommendDto> getRecommend() {
        //로직 오늘 날짜 부터 7일중 가장 좋아요 많은 프로젝트 12개 선정
        // 오늘 날짜
        LocalDate today = LocalDate.now();
        // 7일 이후 날짜
        LocalDate weekAgo = today.minusWeeks(1);

        List<Project> projects = projectRepository.findTopProjectsByLikesInPeriod(today,weekAgo);
        List<ResProjectRecommendDto> result = new ArrayList<>();
        for(Project project : projects) {
            ResProjectRecommendDto resProjectRecommendDto = new ResProjectRecommendDto();
            resProjectRecommendDto.setId(project.getId());
            resProjectRecommendDto.setTitle(project.getTitle());
            resProjectRecommendDto.setDescription(project.getDescription());
            resProjectRecommendDto.setWriteName(project.getUser().getName());
            resProjectRecommendDto.setThumbnailURL(project.getThumbnailURL());
            result.add(resProjectRecommendDto);
        }
        return result;
    }
    @Transactional
    public void projectUpload(ReqProject reqProject) throws IOException {
        //TEST 단계에서 값을 가져옴
        // 실제 배포단계면  securitContectHolder 에 값 가져옴
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            String userEmail;
            if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
                userEmail = authentication.getName();
            } else {
                throw new RuntimeException("로그인이 필요합니다.");
            }

            Optional<User> user = userRepository.findByEmail(aesUtil.encode(userEmail));
            if(user.isPresent()) {
                // Null이 아닐경우 project 에 user 삽입
                Project project = new Project();
                project.setUser(user.get()); // 사입하고
                project.setProject(reqProject);
                if(reqProject.getThumbnailImg() != null){
                    String imgUrl = imgUtil.imgUpload(reqProject.getThumbnailImg());
                    project.setThumbnailURL(imgUrl);
                }else{
                    project.setThumbnailURL(null);
                }
                if(project.getThumbnailURL() == null){
                    project.setDemonstrationVideo(null);
                }else{
                    String demonstrationURL = imgUtil.imgUpload(reqProject.getDemonstrationVideo());
                    project.setDemonstrationVideo(demonstrationURL);
                }


                projectRepository.insertProject(project);
                user.get().addProject(project);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Transactional(readOnly = true)
    public ResProjectDetailDTO projectGetById(Long id) {
        Project project = projectRepository.findById(id);
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        if(userEmail.equals("anonymousUser")){
            log.info("확인 할 수 없는 사용자");
        }else if(userEmail == null){
            log.info("userEmail 이 null 입니다.");
        }
        Optional<User> user = userRepository.findByEmail(aesUtil.encode(userEmail));

        ResProjectDetailDTO dto = new ResProjectDetailDTO();

        // 1. 좋아요/북마크 갯수
        dto.setLikeCount(projectLikeRepository.countById(id));
        dto.setBookMarkCount(projectBookMarkRepository.countById(id));

        // 2. 로그인 사용자 좋아요/북마크 여부
        if (user.isPresent()) {
            Long userId = user.get().getId();
            dto.setLikeCheck(projectLikeRepository.existLike(id, userId));
            dto.setBookMarkCheck(projectBookMarkRepository.existBookMark(id, userId));
        } else {
            dto.setLikeCheck(false);
            dto.setBookMarkCheck(false);
        }

        // 3. 프로젝트 기본 정보
        dto.setId(project.getId());
        dto.setTitle(project.getTitle());
        dto.setDescription(project.getDescription());
        dto.setStartDate(project.getStartDate());
        dto.setEndDate(project.getEndDate());
        dto.setRole(project.getRole());
        dto.setDemonstrationVideoUrl(project.getDemonstrationVideo());
        dto.setDescription(project.getDescription());
        dto.setCreatedAt(project.getCreatedAt());
        dto.setWriteName(
                aesUtil.decode(project.getUser().getNickname())
        );
        dto.setPeople(project.getPeople());

        // 4. 댓글 리스트 변환
        List<ProjectComment> comments = projectCommentRepository.findByProjectId(id);
        List<ResCommentListDTO> commentDTOs = new ArrayList<>();
        for (ProjectComment comment : comments) {
            commentDTOs.add(toDTO(comment));
        }
        dto.setResCommentsDTOList(commentDTOs);

        return dto;
    }

    //  댓글 -> DTO 변환 메서드
    private ResCommentListDTO toDTO(ProjectComment comment) {
        ResCommentListDTO dto = new ResCommentListDTO();
        dto.setId(comment.getId());
        dto.setComment(comment.getComment());
        dto.setUserId(comment.getUser().getId());
        dto.setUserProfileURL(comment.getUser().getProfile());
        dto.setUserWriteName(comment.getUser().getName());

        // 대댓글 변환 (재귀)
        List<ResCommentListDTO> replies = new ArrayList<>();
        for (ProjectComment reply : comment.getReplies()) {
            replies.add(toDTO(reply));
        }
        dto.setReplies(replies);

        return dto;
    }



    // project page 랑 현재 상황 가져옴
    @Transactional(readOnly = true)
    public PageDTO<ResProjectDto> getProjects(int page, int size) {
        // 1) 가드
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);

        // 2) 목록 + 전체 개수
        List<Project> rows = projectRepository.selectByCreateAtDesc(safePage, safeSize);
        long total = projectRepository.selectAllCount();

        // 3) 엔티티 -> DTO
        List<ResProjectDto> content = rows.stream()
                .map(this::toResProjectDto)
                .toList();

        // 4) 페이지 메타 계산
        int totalPages = (int) Math.ceil(total / (double) safeSize);
        boolean first = safePage == 0;
        boolean last = (totalPages == 0) || (safePage >= totalPages - 1);
        boolean hasNext = safePage < totalPages - 1;
        boolean hasPrevious = safePage > 0;
        int count = content.size();

        // 5) PageDTO 생성 후 반환
        return new PageDTO<>(
                content,
                safePage,
                safeSize,
                total,
                totalPages,
                first,
                last,
                hasNext,
                hasPrevious,
                count
        );
    }
    // 프로젝트에 표현할 index표기
    private ResProjectDto toResProjectDto(Project p) {
        // 엔티티 필드명에 맞게 매핑하세요.
        return new ResProjectDto(
                p.getId(),
                p.getTitle(),
                p.getDescription(),
                p.getUser() != null ? p.getUser().getName() : null,
                p.getThumbnailURL()
        );
    }
}
