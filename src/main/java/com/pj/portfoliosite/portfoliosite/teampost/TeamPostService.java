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
import com.pj.portfoliosite.portfoliosite.skill.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final SkillRepository skillRepository;

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
            teamPost.setRecruitDeadline(reqTeamPostDTO.getRecruitDeadline());
            teamPost.setContactMethod(reqTeamPostDTO.getContactMethod());
            teamPost.setSaveStatus(reqTeamPostDTO.isSaveStatus());
            // 스킬은 엔티티 저장 후에 처리

            if (reqTeamPostDTO.getRecruitRoles() != null) {
                for (RecruitRoleDto roleDto : reqTeamPostDTO.getRecruitRoles()) {
                    RecruitRole recruitRole = new RecruitRole();
                    recruitRole.setRole(roleDto.getRole());
                    recruitRole.setCount(roleDto.getCount());
                    
                    // ✅ skillIds를 SkillInfo로 변환하여 JSON 저장
                    System.out.println("=== DEBUG: RecruitRole Skill Processing ===");
                    System.out.println("roleDto.getSkillIds(): " + roleDto.getSkillIds());
                    System.out.println("roleDto.getSkills(): " + roleDto.getSkills());
                    
                    if (roleDto.getSkillIds() != null && !roleDto.getSkillIds().isEmpty()) {
                        System.out.println("Processing skillIds: " + roleDto.getSkillIds());
                        List<RecruitRole.SkillInfo> skillInfos = new ArrayList<>();
                        for (Long skillId : roleDto.getSkillIds()) {
                            Skill skill = skillRepository.findById(skillId)
                                    .orElseThrow(() -> new RuntimeException("Skill not found: " + skillId));
                            skillInfos.add(new RecruitRole.SkillInfo(skill.getId(), skill.getName()));
                        }
                        recruitRole.setSkills(skillInfos);
                        System.out.println("Skills set successfully: " + skillInfos);
                    } else if (roleDto.getSkills() != null && !roleDto.getSkills().isEmpty()) {
                        System.out.println("Processing skills objects directly: " + roleDto.getSkills());
                        List<RecruitRole.SkillInfo> skillInfos = new ArrayList<>();
                        for (RecruitRoleDto.SkillDto skillDto : roleDto.getSkills()) {
                            if (skillDto.getId() != null && skillDto.getName() != null) {
                                skillInfos.add(new RecruitRole.SkillInfo(skillDto.getId(), skillDto.getName()));
                            }
                        }
                        recruitRole.setSkills(skillInfos);
                        System.out.println("Skills set from DTO objects: " + skillInfos);
                    } else {
                        System.out.println("No skills found for this role");
                    }
                    
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
    public PageDTO<ResTeamPostDTO> getTeamPosts(int page, int size, String category) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);

        List<TeamPost> rows;
        long total;

        if (category == null || category.isEmpty() || "ALL".equalsIgnoreCase(category)) {
            // 카테고리 필터링 없음
            rows = teamPostRepository.selectByCreateAtDesc(safePage, safeSize);
            total = teamPostRepository.selectAllCount();
        } else {
            // 카테고리로 필터링
            rows = teamPostRepository.selectByCreateAtDescWithCategory(safePage, safeSize, category);
            total = teamPostRepository.selectCountByCategory(category);
        }

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
        dto.setWriterName(getDecryptedWriterName(teamPost.getUser()));
        dto.setCreatedAt(teamPost.getCreatedAt());
        dto.setRecruitDeadline(teamPost.getRecruitDeadline());
        dto.setContactMethod(teamPost.getContactMethod());
        dto.setSkills(teamPost.getResSkills()); // 중간 엔티티를 통해 ResSkill(스킬 id와 name) 가져오기
        dto.setRecruitStatus(teamPost.getRecruitStatus().toString());
        dto.setViewCount(teamPost.getViewCount());

        dto.setLikeCount(teamPostLikeRepository.countByTeamPostId(id));
        dto.setBookmarkCount(teamPostBookMarkRepository.countByTeamPostId(id));

        if (user != null) {
            Long userId = user.getId();
            Long postOwnerId = teamPost.getUser().getId();
            
            dto.setLiked(teamPostLikeRepository.existLike(id, userId));
            dto.setBookmarked(teamPostBookMarkRepository.existBookMark(id, userId));
            dto.setOwner(userId.equals(postOwnerId));
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
            roleDTOs.add(convertRecruitRoleToDto(role));
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
        try {
            TeamPost teamPost = teamPostRepository.findById(id);
            
            if (teamPost == null) {
                throw new RuntimeException("게시물을 찾을 수 없습니다.");
            }

            teamPost.setTitle(reqTeamPostDTO.getTitle());
            teamPost.setContent(reqTeamPostDTO.getContent());
            teamPost.setRecruitDeadline(reqTeamPostDTO.getRecruitDeadline());
            teamPost.setContactMethod(reqTeamPostDTO.getContactMethod());
            teamPost.setSaveStatus(reqTeamPostDTO.isSaveStatus());
            
            // 기존 recruitRoles 삭제
            teamPost.getRecruitRoles().clear();
            
            // 새로운 recruitRoles 추가
            if (reqTeamPostDTO.getRecruitRoles() != null) {
                for (RecruitRoleDto roleDto : reqTeamPostDTO.getRecruitRoles()) {
                    RecruitRole recruitRole = new RecruitRole();
                    recruitRole.setRole(roleDto.getRole());
                    recruitRole.setCount(roleDto.getCount());
                    recruitRole.setPeople(roleDto.getPeople());
                    
                    System.out.println("=== DEBUG: RecruitRole Update Skill Processing ===");
                    System.out.println("roleDto.getSkillIds(): " + roleDto.getSkillIds());
                    System.out.println("roleDto.getSkills(): " + roleDto.getSkills());
                    
                    if (roleDto.getSkillIds() != null && !roleDto.getSkillIds().isEmpty()) {
                        System.out.println("Processing skillIds: " + roleDto.getSkillIds());
                        List<RecruitRole.SkillInfo> skillInfos = new ArrayList<>();
                        for (Long skillId : roleDto.getSkillIds()) {
                            Skill skill = skillRepository.findById(skillId)
                                    .orElseThrow(() -> new RuntimeException("Skill not found: " + skillId));
                            skillInfos.add(new RecruitRole.SkillInfo(skill.getId(), skill.getName()));
                        }
                        recruitRole.setSkills(skillInfos);
                        System.out.println("Skills set successfully: " + skillInfos);
                    } else if (roleDto.getSkills() != null && !roleDto.getSkills().isEmpty()) {
                        System.out.println("Processing skills objects directly: " + roleDto.getSkills());
                        List<RecruitRole.SkillInfo> skillInfos = new ArrayList<>();
                        for (RecruitRoleDto.SkillDto skillDto : roleDto.getSkills()) {
                            if (skillDto.getId() != null && skillDto.getName() != null) {
                                skillInfos.add(new RecruitRole.SkillInfo(skillDto.getId(), skillDto.getName()));
                            }
                        }
                        recruitRole.setSkills(skillInfos);
                        System.out.println("Skills set from DTO objects: " + skillInfos);
                    } else {
                        System.out.println("No skills found for this role");
                    }
                    
                    teamPost.addRecruitRole(recruitRole);
                }
            }
            
            // 스킬 업데이트
            if (reqTeamPostDTO.getSkills() != null) {
                teamPostSkillService.updateTeamPostSkills(teamPost, reqTeamPostDTO.getSkills());
            }

            teamPostRepository.updateTeamPost(teamPost);
            
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("게시물 수정 중 오류가 발생했습니다.", e);
        }
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
        dto.setWriterName(getDecryptedWriterName(teamPost.getUser()));
        dto.setCreatedAt(teamPost.getCreatedAt());
        dto.setRecruitDeadline(teamPost.getRecruitDeadline());
        dto.setRecruitStatus(teamPost.getRecruitStatus().toString());
        dto.setViewCount(teamPost.getViewCount());
        dto.setLikeCount(teamPost.getLikes().size());

        // ✅ convertRecruitRoleToDto() 메서드 사용
        List<RecruitRoleDto> requiredRoles = new ArrayList<>();
        for (RecruitRole role : teamPost.getRecruitRoles()) {
            requiredRoles.add(convertRecruitRoleToDto(role));
        }
        dto.setRequiredRoles(requiredRoles);

        return dto;
    }

    private RecruitRoleDto convertRecruitRoleToDto(RecruitRole role) {
        RecruitRoleDto dto = new RecruitRoleDto();
        dto.setId(role.getId());
        dto.setRole(role.getRole());
        dto.setCount(role.getCount());
        dto.setPeople(role.getPeople());
        
        if (role.getSkills() != null && !role.getSkills().isEmpty()) {
            List<RecruitRoleDto.SkillDto> skillDtos = role.getSkills().stream()
                    .map(skillInfo -> RecruitRoleDto.SkillDto.builder()
                            .id(skillInfo.getId())
                            .name(skillInfo.getName())
                            .build())
                    .collect(Collectors.toList());
            dto.setSkills(skillDtos);
        }
        
        return dto;
    }

    private ResTeamCommentListDTO toCommentDTO(TeamPostComment comment) {
        ResTeamCommentListDTO dto = new ResTeamCommentListDTO();
        dto.setId(comment.getId());
        dto.setComment(comment.getComment());
        dto.setUserId(comment.getUser().getId());
        dto.setUserProfileURL(comment.getUser().getProfile());
        dto.setUserWriteName(getDecryptedWriterName(comment.getUser()));

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

    // 마이페이지용: 전체 팀포스트 목록 조회 (페이지네이션)
    @Transactional(readOnly = true)
    public PageDTO<ResTeamPostDTO> getAllTeamPosts(int page, int size) {
        // 1) 입력 값 검증 및 기본값 설정
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);

        // 2) Repository에서 데이터 목록과 전체 개수 조회
        List<TeamPost> rows = teamPostRepository.selectAllTeamPosts(safePage, safeSize);
        long totalElements = teamPostRepository.selectAllTeamPostsCount();

        // 3) 조회된 엔티티 목록을 DTO 목록으로 변환
        List<ResTeamPostDTO> content = rows.stream()
                .map(this::toResTeamPostDTO)
                .toList();

        // 4) 페이지네이션 메타 데이터 계산
        int totalPages = (int) Math.ceil((double) totalElements / safeSize);
        boolean first = safePage == 0;
        boolean last = (totalPages == 0) || (safePage >= totalPages - 1);
        boolean hasNext = !last;
        boolean hasPrevious = !first;
        int count = content.size();

        // 5) 최종 PageDTO 객체를 생성하여 반환
        return new PageDTO<>(
                content,
                safePage,
                safeSize,
                totalElements,
                totalPages,
                first,
                last,
                hasNext,
                hasPrevious,
                count
        );
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
                // 암호화 시도 실패 시 평문으로 계속 진행
            }

            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                return userOpt.get();
            }

            // 마이그레이션 필요 시 모든 사용자 조회
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
                        } catch (Exception e) {
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

    // User의 nickname 또는 name을 복호화하여 반환
    private String getDecryptedWriterName(User user) {
        if (user == null) {
            return null;
        }
        
        try {
            // nickname 우선 시도
            String nickname = user.getNickname();
            if (nickname != null && !nickname.isEmpty()) {
                try {
                    String decryptedNickname = personalDataUtil.decryptPersonalData(nickname);
                    // 복호화가 성공하고 결과가 다르면 복호화된 값 사용
                    if (!decryptedNickname.equals(nickname)) {
                        return decryptedNickname;
                    }
                    // 복호화 결과가 같으면 이미 평문
                    return nickname;
                } catch (Exception e) {
                    // 복호화 실패 시 평문으로 간주
                    return nickname;
                }
            }
            
            // nickname이 없으면 name 사용
            String name = user.getName();
            if (name != null && !name.isEmpty()) {
                try {
                    String decryptedName = personalDataUtil.decryptPersonalData(name);
                    if (!decryptedName.equals(name)) {
                        return decryptedName;
                    }
                    return name;
                } catch (Exception e) {
                    return name;
                }
            }
            
            return "Unknown";
        } catch (Exception e) {
            return "Unknown";
        }
    }
}
