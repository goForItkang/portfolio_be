package com.pj.portfoliosite.portfoliosite.teampost;

import com.pj.portfoliosite.portfoliosite.global.dto.PageDTO;
import com.pj.portfoliosite.portfoliosite.global.dto.RecruitRoleDto;
import com.pj.portfoliosite.portfoliosite.global.entity.*;
import com.pj.portfoliosite.portfoliosite.teampost.bookmark.TeamPostBookMarkRepository;
import com.pj.portfoliosite.portfoliosite.teampost.comment.TeamPostCommentRepository;
import com.pj.portfoliosite.portfoliosite.teampost.dto.*;
import com.pj.portfoliosite.portfoliosite.teampost.like.TeamPostLikeRepository;
import com.pj.portfoliosite.portfoliosite.teampost.skill.TeamPostSkillService;
import com.pj.portfoliosite.portfoliosite.user.UserRepository;
import com.pj.portfoliosite.portfoliosite.util.PersonalDataUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamPostService {
    private final TeamPostRepository teamPostRepository;
    private final UserRepository userRepository;
    private final TeamPostCommentRepository teamPostCommentRepository;
    private final TeamPostLikeRepository teamPostLikeRepository;
    private final TeamPostBookMarkRepository teamPostBookMarkRepository;
    private final TeamPostSkillService teamPostSkillService;
    private final PersonalDataUtil personalDataUtil;

    // 팀원 구하기 저장
    @Transactional
    public void saveTeamPost(ReqTeamPostDTO reqTeamPostDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            String userEmail;
            if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
                userEmail = authentication.getName();
            } else {
                throw new RuntimeException("로그인이 필요합니다.");
            }

            User user = findUserByEmailSafely(userEmail);
            if (user == null) {
                throw new RuntimeException("사용자를 찾을 수 없습니다: " + userEmail);
            }

            TeamPost teamPost = new TeamPost();
            teamPost.setUser(user);
            teamPost.setTitle(reqTeamPostDTO.getTitle());
            teamPost.setContent(reqTeamPostDTO.getContent());
            teamPost.setProjectType(reqTeamPostDTO.getProjectType());
            teamPost.setRecruitDeadline(reqTeamPostDTO.getRecruitDeadline());
            teamPost.setContactMethod(reqTeamPostDTO.getContactMethod());
            teamPost.setSaveStatus(reqTeamPostDTO.isSaveStatus());
            // 스킬은 엔티티 저장 후에 처리

            if (reqTeamPostDTO.getRecruitRoles() != null) {
                for (RecruitRoleDto roleDto : reqTeamPostDTO.getRecruitRoles()) {
                    RecruitRole recruitRole = new RecruitRole();
                    recruitRole.setRole(roleDto.getRole());
                    recruitRole.setCount(roleDto.getCount());
                    teamPost.addRecruitRole(recruitRole);
                }
            }

            teamPostRepository.insertTeamPost(teamPost);
            user.addTeamPost(teamPost);
            
            // 스킬 추가
            if (reqTeamPostDTO.getSkills() != null && !reqTeamPostDTO.getSkills().isEmpty()) {
                teamPostSkillService.addSkillsToTeamPost(teamPost, reqTeamPostDTO.getSkills());
            }
            
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("게시글 저장 중 오류가 발생했습니다.", e);
        }
    }

    // 팀원 구하기 목록
    @Transactional(readOnly = true)
    public PageDTO<ResTeamPostDTO> getTeamPosts(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);

        List<TeamPost> rows = teamPostRepository.selectByCreateAtDesc(safePage, safeSize);
        long total = teamPostRepository.selectAllCount();

        List<ResTeamPostDTO> content = rows.stream()
                .map(this::toResTeamPostDTO)
                .toList();

        int totalPages = (int) Math.ceil(total / (double) safeSize);
        boolean first = safePage == 0;
        boolean last = (totalPages == 0) || (safePage >= totalPages - 1);
        boolean hasNext = safePage < totalPages - 1;
        boolean hasPrevious = safePage > 0;
        int count = content.size();

        return new PageDTO<>(
                content, safePage, safeSize, total, totalPages,
                first, last, hasNext, hasPrevious, count
        );
    }

    @Transactional
    public ResTeamPostDetailDTO getTeamPostById(Long id) {
        TeamPost teamPost = teamPostRepository.findById(id);

        if (teamPost == null) {
            throw new RuntimeException("해당 ID의 팀포스트를 찾을 수 없습니다: " + id);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = null;
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            userEmail = authentication.getName();
        }

        User user = userEmail != null ? findUserByEmailSafely(userEmail) : null;

        teamPost.increaseViewCount();
        teamPostRepository.updateTeamPost(teamPost);

        ResTeamPostDetailDTO dto = new ResTeamPostDetailDTO();

        dto.setId(teamPost.getId());
        dto.setTitle(teamPost.getTitle());
        dto.setContent(teamPost.getContent());
        dto.setWriterName(teamPost.getUser().getName());
        dto.setProjectType(teamPost.getProjectType());
        dto.setCreatedAt(teamPost.getCreatedAt());
        dto.setRecruitDeadline(teamPost.getRecruitDeadline());
        dto.setContactMethod(teamPost.getContactMethod());
        dto.setSkills(teamPost.getSkillNames()); // 중간 엔티티를 통해 스킬 이름 가져오기
        dto.setRecruitStatus(teamPost.getRecruitStatus().toString());
        dto.setViewCount(teamPost.getViewCount());

        dto.setLikeCount(teamPostLikeRepository.countByTeamPostId(id));
        dto.setBookmarkCount(teamPostBookMarkRepository.countByTeamPostId(id));

        if (user != null) {
            Long userId = user.getId();
            dto.setLiked(teamPostLikeRepository.existLike(id, userId));
            dto.setBookmarked(teamPostBookMarkRepository.existBookMark(id, userId));
            dto.setOwner(teamPost.getUser().getId().equals(userId));
        } else {
            dto.setLiked(false);
            dto.setBookmarked(false);
            dto.setOwner(false);
        }

        List<TeamPostComment> comments = teamPostCommentRepository.findByTeamPostId(id);
        List<ResTeamCommentListDTO> commentDTOs = new ArrayList<>();
        for (TeamPostComment comment : comments) {
            commentDTOs.add(toCommentDTO(comment));
        }
        dto.setComments(commentDTOs);

        List<RecruitRoleDto> roleDTOs = new ArrayList<>();
        for (RecruitRole role : teamPost.getRecruitRoles()) {
            RecruitRoleDto roleDto = new RecruitRoleDto();
            roleDto.setRole(role.getRole());
            roleDto.setCount(role.getCount());
            roleDTOs.add(roleDto);
        }
        dto.setRecruitRoles(roleDTOs);

        return dto;
    }

    // 사용자의 임시저장 게시물 조회
    public List<ResTeamPostDTO> getUserDrafts() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
                return new ArrayList<>();
            }

            String userEmail = authentication.getName();
            User user = findUserByEmailSafely(userEmail);

            if (user != null) {
                return teamPostRepository.findDraftsByUserId(user.getId());
            }

            return new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // 임시저장 게시물 정식 발행
    @Transactional
    public String publishDraft(Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
                return "로그인이 필요합니다.";
            }

            String userEmail = authentication.getName();
            User user = findUserByEmailSafely(userEmail);

            if (user == null) {
                return "사용자를 찾을 수 없습니다.";
            }

            TeamPost teamPost = teamPostRepository.findById(id);

            if (teamPost == null) {
                return "게시물을 찾을 수 없습니다.";
            }

            if (!teamPost.getUser().getId().equals(user.getId())) {
                return "게시물을 발행할 권한이 없습니다.";
            }

            if (!teamPost.isSaveStatus()) {
                return "이미 발행된 게시물입니다.";
            }

            teamPost.setSaveStatus(false);
            teamPost.setCreatedAt(java.time.LocalDateTime.now());
            teamPostRepository.updateTeamPost(teamPost);

            return "게시물이 정식 발행되었습니다.";

        } catch (Exception e) {
            return "발행 처리 중 오류가 발생했습니다.";
        }
    }

    // 임시저장 게시물 삭제
    @Transactional
    public String deleteDraft(Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
                return "로그인이 필요합니다.";
            }

            String userEmail = authentication.getName();
            User user = findUserByEmailSafely(userEmail);

            if (user == null) {
                return "사용자를 찾을 수 없습니다.";
            }

            TeamPost teamPost = teamPostRepository.findById(id);

            if (teamPost == null) {
                return "게시물을 찾을 수 없습니다.";
            }

            if (!teamPost.getUser().getId().equals(user.getId())) {
                return "게시물을 삭제할 권한이 없습니다.";
            }

            if (!teamPost.isSaveStatus()) {
                return "임시저장된 게시물이 아닙니다.";
            }

            teamPostRepository.deleteTeamPost(id);

            return "임시저장 게시물이 삭제되었습니다.";

        } catch (Exception e) {
            return "삭제 처리 중 오류가 발생했습니다.";
        }
    }

    // 팀원 구하기 수정
    @Transactional
    public void updateTeamPost(Long id, ReqTeamPostDTO reqTeamPostDTO) {
        TeamPost teamPost = teamPostRepository.findById(id);

        teamPost.setTitle(reqTeamPostDTO.getTitle());
        teamPost.setContent(reqTeamPostDTO.getContent());
        teamPost.setProjectType(reqTeamPostDTO.getProjectType());
        teamPost.setRecruitDeadline(reqTeamPostDTO.getRecruitDeadline());
        teamPost.setContactMethod(reqTeamPostDTO.getContactMethod());
        teamPost.setSaveStatus(reqTeamPostDTO.isSaveStatus());
        // 스킬 업데이트
        if (reqTeamPostDTO.getSkills() != null) {
            teamPostSkillService.updateTeamPostSkills(teamPost, reqTeamPostDTO.getSkills());
        }

        teamPostRepository.updateTeamPost(teamPost);
    }

    // 팀원 구하기 삭제
    @Transactional
    public void deleteTeamPost(Long id) {
        teamPostRepository.deleteTeamPost(id);
    }

    // DTO 변환 메서드들
    private ResTeamPostDTO toResTeamPostDTO(TeamPost teamPost) {
        ResTeamPostDTO dto = new ResTeamPostDTO();
        dto.setId(teamPost.getId());
        dto.setTitle(teamPost.getTitle());
        dto.setWriterName(teamPost.getUser() != null ? teamPost.getUser().getName() : null);
        dto.setProjectType(teamPost.getProjectType());
        dto.setCreatedAt(teamPost.getCreatedAt());
        dto.setRecruitStatus(teamPost.getRecruitStatus().toString());
        dto.setViewCount(teamPost.getViewCount());
        dto.setLikeCount(teamPost.getLikes().size());

        List<String> requiredRoles = new ArrayList<>();
        for (RecruitRole role : teamPost.getRecruitRoles()) {
            requiredRoles.add(role.getRole());
        }
        dto.setRequiredRoles(requiredRoles);

        return dto;
    }

    private ResTeamCommentListDTO toCommentDTO(TeamPostComment comment) {
        ResTeamCommentListDTO dto = new ResTeamCommentListDTO();
        dto.setId(comment.getId());
        dto.setComment(comment.getComment());
        dto.setUserId(comment.getUser().getId());
        dto.setUserProfileURL(comment.getUser().getProfile());
        dto.setUserWriteName(comment.getUser().getName());

        List<ResTeamCommentListDTO> replies = new ArrayList<>();
        for (TeamPostComment reply : comment.getReplies()) {
            replies.add(toCommentDTO(reply));
        }
        dto.setReplies(replies);

        return dto;
    }

    // 메인 페이지용: 일주일간 좋아요를 많이 받은 TeamPost 4개 조회
    @Transactional(readOnly = true)
    public List<ResTeamPostDTO> getTop4TeamPostsByLikes() {
        List<TeamPost> topPosts = teamPostRepository.findTop4ByLikesInLastWeek();
        return topPosts.stream()
                .map(this::toResTeamPostDTO)
                .toList();
    }

    private User findUserByEmailSafely(String email) {
        try {
            try {
                String encryptedEmail = personalDataUtil.encryptPersonalData(email);
                Optional<User> userOpt = userRepository.findByEmail(encryptedEmail);
                if (userOpt.isPresent()) {
                    return userOpt.get();
                }
            } catch (Exception e) {
                // 암호화된 이메일 검색 실패 시 다음 단계로
            }

            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                return userOpt.get();
            }

            List<User> allUsers = userRepository.findAllForMigration();
            for (User user : allUsers) {
                try {
                    String userEmail = user.getEmail();
                    if (userEmail != null) {
                        if (email.equals(userEmail)) {
                            return user;
                        }
                        
                        try {
                            String decryptedEmail = personalDataUtil.decryptPersonalData(userEmail);
                            if (email.equals(decryptedEmail)) {
                                return user;
                            }
                        } catch (Exception decryptError) {
                            // 복호화 실패는 무시
                        }
                    }
                } catch (Exception e) {
                    // 개별 사용자 처리 실패 무시
                }
            }
            
            return null;
            
        } catch (Exception e) {
            return null;
        }
    }
}