package com.pj.portfoliosite.portfoliosite.project;

import com.pj.portfoliosite.portfoliosite.blog.bookmark.BookmarkService;
import com.pj.portfoliosite.portfoliosite.global.dto.*;
import com.pj.portfoliosite.portfoliosite.global.entity.*;
import com.pj.portfoliosite.portfoliosite.project.bookmark.ProjectBookMarkRepository;
import com.pj.portfoliosite.portfoliosite.project.comment.ProjectCommentRepository;
import com.pj.portfoliosite.portfoliosite.project.dto.ResProjectDetailDto;
import com.pj.portfoliosite.portfoliosite.project.like.ProjectLikeRepository;
import com.pj.portfoliosite.portfoliosite.project.like.ProjectLikeService;
import com.pj.portfoliosite.portfoliosite.skill.ResSkill;
import com.pj.portfoliosite.portfoliosite.skill.SkillRepository;
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
    private final SkillRepository skillRepository;
    private final BookmarkService bookmarkService;

    //추천 프로젝트 로직 오늘 부터 일주일 동안 가장 많은 좋아요 갯수
    public List<ResProjectRecommendDto> getRecommend() {
        // 오늘 날짜와 1주일 전 날짜 계산
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusWeeks(1);

        // 1️⃣ 최근 일주일 동안 좋아요가 많은 프로젝트 조회
        List<Project> projects = projectRepository.findTopProjectsByLikesInPeriod(today, weekAgo);

        List<ResProjectRecommendDto> result = new ArrayList<>();

        // 2️⃣ 1차 프로젝트 → DTO 변환
        for (Project project : projects) {
            ResProjectRecommendDto dto = new ResProjectRecommendDto();
            dto.setId(project.getId());
            dto.setTitle(project.getTitle());
            dto.setDescription(project.getDescription());
            dto.setWriteName(aesUtil.decode(project.getUser().getNickname()));
            dto.setThumbnailURL(project.getThumbnailURL() != null ? project.getThumbnailURL() : "card.png");
            dto.setRole(project.getRole());
            result.add(dto);
        }

        // 3️⃣ 4개 미만일 경우, 부족한 만큼 추가 (중복 제거 포함)
        if (result.size() < 4) {
            int size = 4 - result.size();

            // 이미 포함된 ID는 제외
            List<Long> existingIds = projects.stream()
                    .map(Project::getId)
                    .toList();

            // 좋아요 순으로 추가 조회 (기존 ID 제외)
            List<Project> projects2 = projectRepository.findTopByLikeDescExcludeIds(existingIds, size);

            for (Project project : projects2) {
                // 혹시라도 중복 방지
                if (existingIds.contains(project.getId())) continue;

                ResProjectRecommendDto dto = new ResProjectRecommendDto();
                dto.setId(project.getId());
                dto.setTitle(project.getTitle());
                dto.setDescription(project.getDescription());
                dto.setWriteName(aesUtil.decode(project.getUser().getNickname()));
                dto.setThumbnailURL(project.getThumbnailURL() != null ? project.getThumbnailURL() : "card.png");
                dto.setRole(project.getRole());
                result.add(dto);
            }
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
                List<String> skillIds = reqProject.getSkillIds();

                if(skillIds != null && !skillIds.isEmpty()) {
                    List<ProjectSkill> projectSkills = new ArrayList<>();
                    for (String skillIdString : skillIds) {
                        try{
                            Long skillId = Long.parseLong(skillIdString);
                            Skill skillReference = skillRepository.getReferenceById(skillId);

                            ProjectSkill projectSkill = new ProjectSkill();
                            projectSkill.setSkill(skillReference);
                            projectSkills.add(projectSkill);
                        } catch (NumberFormatException e) {
                            // 만약 skillIdString이 숫자로 변환될 수 없는 값이라면, 이 예외가 발생합니다.
                            // 해당 스킬은 무시하고 계속 진행하거나, 로그를 남길 수 있습니다.
                            log.warn("숫자로 변환할 수 없는 Skill ID를 건너뜁니다: {}", skillIdString);
                        }

                    }
                    project.addSkill(projectSkills);
                }
                if(reqProject.getThumbnailImg() != null){
                    String imgUrl = imgUtil.imgUpload(reqProject.getThumbnailImg());
                    project.setThumbnailURL(imgUrl);
                }else{
                    project.setThumbnailURL(null);
                }
                if(reqProject.getDemonstrationVideo() == null){
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
        List<Skill> projectSkills = skillRepository.selectByProjectId(id);
        ResSkill resSkill = new ResSkill();
        dto.setSkills(resSkill.toResSkillList(projectSkills));
        // 1. 좋아요/북마크 갯수
        dto.setLikeCount(projectLikeRepository.countById(id));
        dto.setBookMarkCount(projectBookMarkRepository.countById(id));

        // 2. 로그인 사용자 좋아요/북마크 여부
        if (user.isPresent()) {
            Long userId = user.get().getId();
            dto.setLikeCheck(projectLikeRepository.existLike(id, userId));
            dto.setBookMarkCheck(projectBookMarkRepository.existBookMark(id, userId));
            if(project.getUser().getId().equals(userId)){
                dto.setOwner(true);
            }
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
        dto.setDistribution(project.getDistribution());

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

        List<ResProjectDto> content = new ArrayList<>();
        for (Project row : rows) {
            ResProjectDto dto = new ResProjectDto();
            dto.setId(row.getId());
            dto.setTitle(row.getTitle());
            dto.setRole(row.getRole());
            dto.setThumbnailURL(row.getThumbnailURL());
            dto.setWriteName(aesUtil.decode(row.getUser().getNickname()));
            content.add(dto);
        }

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
                p.getRole(),
                p.getUser() != null ? p.getUser().getName() : null,
                p.getThumbnailURL()
        );
    }

    public void delete(Long id) {
        projectRepository.deleteByid(id);
    }
    // 오류 해결 해야함
    public ResProjectDetailDto projectDetailsById(Long id) {
        ResProjectDetailDto dto = new ResProjectDetailDto();
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userRepository.findByEmail(aesUtil.encode(email));
        dto.setBookMarkCount(projectBookMarkRepository.countById(id));
        dto.setLikeCount(projectLikeRepository.countById(id));
        if(user.isPresent()){
            dto.setLikeCheck(projectLikeRepository.existLike(id,user.get().getId()));
            dto.setBookMarkCheck(projectBookMarkRepository.existBookMark(id,user.get().getId()));
        }else{
            dto.setBookMarkCheck(false);
            dto.setLikeCheck(false);
        }
        return dto;
    }
    @Transactional
    public void update(Long id, ReqProject reqProject) throws IOException {
        Project project = projectRepository.findById(id);
        project.updateProject(reqProject);
        if(reqProject.getThumbnailImg() != null){
            project.setThumbnailURL(imgUtil.imgUpload(reqProject.getThumbnailImg()));
        }
        if(reqProject.getDemonstrationVideo() != null){
            project.setDemonstrationVideo(imgUtil.imgUpload(reqProject.getDemonstrationVideo()));
        }
        if(reqProject.getSkillIds() != null){
            List<ProjectSkill> projectSkills = new ArrayList<>();

            }

        List<String> skillIds = reqProject.getSkillIds();
        if(skillIds != null && !skillIds.isEmpty()) {
            List<ProjectSkill> projectSkills = new ArrayList<>();
            for (String skillIdString : skillIds) {
                try{
                    Long skillId = Long.parseLong(skillIdString);
                    Skill skillReference = skillRepository.getReferenceById(skillId);

                    ProjectSkill projectSkill = new ProjectSkill();
                    projectSkill.setSkill(skillReference);
                    projectSkills.add(projectSkill);
                } catch (NumberFormatException e) {
                    // 만약 skillIdString이 숫자로 변환될 수 없는 값이라면, 이 예외가 발생합니다.
                    // 해당 스킬은 무시하고 계속 진행하거나, 로그를 남길 수 있습니다.
                    log.warn("숫자로 변환할 수 없는 Skill ID를 건너뜁니다: {}", skillIdString);
                }

            }
            project.addSkill(projectSkills);
        }


    }
}
