package com.pj.portfoliosite.portfoliosite.teampost;

import com.pj.portfoliosite.portfoliosite.global.dto.PageDTO;
import com.pj.portfoliosite.portfoliosite.global.dto.RecruitRoleDto;
import com.pj.portfoliosite.portfoliosite.global.entity.*;
import com.pj.portfoliosite.portfoliosite.teampost.bookmark.TeamPostBookMarkRepository;
import com.pj.portfoliosite.portfoliosite.teampost.comment.TeamPostCommentRepository;
import com.pj.portfoliosite.portfoliosite.teampost.dto.*;
import com.pj.portfoliosite.portfoliosite.teampost.like.TeamPostLikeRepository;
import com.pj.portfoliosite.portfoliosite.user.UserRepository;
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

    // 팀원 구하기 저장
    @Transactional
    public void saveTeamPost(ReqTeamPostDTO reqTeamPostDTO) {
        // 현재 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        String userEmail;
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            userEmail = authentication.getName();
        } else {
            throw new RuntimeException("로그인이 필요합니다.");
        }
        
        Optional<User> user = userRepository.findByEmail(userEmail);
        
        if (user.isEmpty()) {
            throw new RuntimeException("사용자를 찾을 수 없습니다: " + userEmail);
        }

        if (user.isPresent()) {
            TeamPost teamPost = new TeamPost();
            teamPost.setUser(user.get());
            teamPost.setTitle(reqTeamPostDTO.getTitle());
            teamPost.setContent(reqTeamPostDTO.getContent());
            teamPost.setProjectType(reqTeamPostDTO.getProjectType());
            teamPost.setRecruitDeadline(reqTeamPostDTO.getRecruitDeadline());
            teamPost.setContactMethod(reqTeamPostDTO.getContactMethod());
            teamPost.setSaveStatus(reqTeamPostDTO.isSaveStatus());
            teamPost.setSkills(reqTeamPostDTO.getSkills());

            // 모집 역할 추가
            if (reqTeamPostDTO.getRecruitRoles() != null) {
                for (RecruitRoleDto roleDto : reqTeamPostDTO.getRecruitRoles()) {
                    RecruitRole recruitRole = new RecruitRole();
                    recruitRole.setRole(roleDto.getRole());
                    recruitRole.setCount(roleDto.getCount());
                    teamPost.addRecruitRole(recruitRole);
                }
            }

            teamPostRepository.insertTeamPost(teamPost);
            user.get().addTeamPost(teamPost);
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

    // 팀원 구하기 상세
    @Transactional
    public ResTeamPostDetailDTO getTeamPostById(Long id) {
        TeamPost teamPost = teamPostRepository.findById(id);
        
        // null 체크 추가
        if (teamPost == null) {
            throw new RuntimeException("해당 ID의 팀포스트를 찾을 수 없습니다: " + id);
        }
        
        // 현재 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = null;
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            userEmail = authentication.getName();
        }
        
        Optional<User> user = userEmail != null ? userRepository.findByEmail(userEmail) : Optional.empty();

        // 조회수 증가
        teamPost.increaseViewCount();
        teamPostRepository.updateTeamPost(teamPost);

        ResTeamPostDetailDTO dto = new ResTeamPostDetailDTO();

        // 기본 정보
        dto.setId(teamPost.getId());
        dto.setTitle(teamPost.getTitle());
        dto.setContent(teamPost.getContent());
        dto.setWriterName(teamPost.getUser().getName());
        dto.setProjectType(teamPost.getProjectType());
        dto.setCreatedAt(teamPost.getCreatedAt());
        dto.setRecruitDeadline(teamPost.getRecruitDeadline());
        dto.setContactMethod(teamPost.getContactMethod());
        dto.setSkills(teamPost.getSkills());
        dto.setRecruitStatus(teamPost.getRecruitStatus().toString());
        dto.setViewCount(teamPost.getViewCount());

        // 좋아요/북마크 수
        dto.setLikeCount(teamPostLikeRepository.countByTeamPostId(id));
        dto.setBookmarkCount(teamPostBookMarkRepository.countByTeamPostId(id));

        // 사용자별 상태
        if (user.isPresent()) {
            Long userId = user.get().getId();
            dto.setLiked(teamPostLikeRepository.existLike(id, userId));
            dto.setBookmarked(teamPostBookMarkRepository.existBookMark(id, userId));
            dto.setOwner(teamPost.getUser().getId().equals(userId));
        } else {
            dto.setLiked(false);
            dto.setBookmarked(false);
            dto.setOwner(false);
        }

        // 댓글 목록
        List<TeamPostComment> comments = teamPostCommentRepository.findByTeamPostId(id);
        List<ResTeamCommentListDTO> commentDTOs = new ArrayList<>();
        for (TeamPostComment comment : comments) {
            commentDTOs.add(toCommentDTO(comment));
        }
        dto.setComments(commentDTOs);

        // 모집 역할 목록
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
            Optional<User> user = userRepository.findByEmail(userEmail);
            
            if (user.isPresent()) {
                return teamPostRepository.findDraftsByUserId(user.get().getId());
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
            Optional<User> user = userRepository.findByEmail(userEmail);
            
            if (user.isEmpty()) {
                return "사용자를 찾을 수 없습니다.";
            }
            
            TeamPost teamPost = teamPostRepository.findById(id);
            
            if (teamPost == null) {
                return "게시물을 찾을 수 없습니다.";
            }
            
            if (!teamPost.getUser().getId().equals(user.get().getId())) {
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
            Optional<User> user = userRepository.findByEmail(userEmail);
            
            if (user.isEmpty()) {
                return "사용자를 찾을 수 없습니다.";
            }
            
            TeamPost teamPost = teamPostRepository.findById(id);
            
            if (teamPost == null) {
                return "게시물을 찾을 수 없습니다.";
            }
            
            if (!teamPost.getUser().getId().equals(user.get().getId())) {
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
        teamPost.setSkills(reqTeamPostDTO.getSkills());

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

        // 대댓글 처리
        List<ResTeamCommentListDTO> replies = new ArrayList<>();
        for (TeamPostComment reply : comment.getReplies()) {
            replies.add(toCommentDTO(reply));
        }
        dto.setReplies(replies);

        return dto;
    }
}