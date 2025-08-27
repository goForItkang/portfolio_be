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
import com.pj.portfoliosite.portfoliosite.util.ImgUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        String testLoginId = "portfolio@naver.com";
        Optional<User> user = userRepository.findByEmail(testLoginId);
        if(user.isPresent()) {
            // Null이 아닐경우 project 에 user 삽입
            Project project = new Project();
            project.setUser(user.get()); // 사입하고
            project.setProject(reqProject);
            String imgUrl = imgUtil.imgUpload(reqProject.getThumbnailImg());
            String demonstrationURL = imgUtil.imgUpload(reqProject.getDemonstrationVideo());
            project.setThumbnailURL(imgUrl);
            project.setDemonstrationVideo(demonstrationURL);
            projectRepository.insertProject(project);
            user.get().addProject(project);
        }
    }
    // 프로젝트 상세 페이지 가져오기
    public ResProjectDetailDTO projectGetById(Long id) {
        Project project = projectRepository.findById(id);
        // 사용자 ID 가져오기
        String testLoginId = "portfolio@naver.com";
        Optional<User> user = userRepository.findByEmail(testLoginId);
        ResProjectDetailDTO resProjectDetailDTO = new ResProjectDetailDTO(); // 객체 생성
        // logic
        //1. 좋아요 갯수
         Long likeCount = projectLikeRepository.countById(id);
         resProjectDetailDTO.setLikeCount(likeCount);
         Long bookMarkCount = projectBookMarkRepository.countById(id);
         resProjectDetailDTO.setBookMarkCount(bookMarkCount);
        //2. 북 마크 갯수 갯수
        // 로그인한 사용자는 무조건 북마크 및 좋아요가 false 일경우 이고, 로그인 한 사람은 체크 여부 확인해야함
        if(user.isPresent()) {
            boolean likeCheck = projectLikeRepository.existLike(id,user.get().getId());
            boolean bookMarkCheck = projectBookMarkRepository.existBookMark(id,user.get().getId());
            resProjectDetailDTO.setLikeCheck(likeCheck); // like
            resProjectDetailDTO.setBookMarkCheck(bookMarkCheck); // bookMark
        } // 사용자가 없을 경우 bookmark 및 like 는 false 로 변경해서 보낼예정임
        else{
            resProjectDetailDTO.setLikeCheck(false);
            resProjectDetailDTO.setBookMarkCheck(false);
        } // 사용자 정보를 찾아서 logic bookmark like
        //댓글 가져오기
        // 나머지 로직에선 북마크 및 좋아요 등 구현
        resProjectDetailDTO.setId(project.getId());
        resProjectDetailDTO.setTitle(project.getTitle());
        resProjectDetailDTO.setDescription(project.getDescription());
        resProjectDetailDTO.setStartDate(project.getStartDate());
        resProjectDetailDTO.setEndDate(project.getEndDate());
        resProjectDetailDTO.setRole(project.getRole());
        resProjectDetailDTO.setDemonstrationVideoUrl(project.getDemonstrationVideo());
        List<ProjectComment> projectComments = projectCommentRepository.findByProjectId(id);
        List<ResCommentListDTO> resCommentListDTOList = new ArrayList<>();
        for(ProjectComment projectComment : projectComments) {
            ResCommentListDTO resCommentListDTO = new ResCommentListDTO();
            resCommentListDTO.setId(projectComment.getId());
            resCommentListDTO.setComment(projectComment.getComment());
            resCommentListDTOList.add(resCommentListDTO);
        }
        return resProjectDetailDTO;
    }
    // comment List 변환



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
