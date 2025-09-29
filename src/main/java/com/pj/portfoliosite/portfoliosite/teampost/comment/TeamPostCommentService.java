package com.pj.portfoliosite.portfoliosite.teampost.comment;

import com.pj.portfoliosite.portfoliosite.global.entity.TeamPost;
import com.pj.portfoliosite.portfoliosite.global.entity.TeamPostComment;
import com.pj.portfoliosite.portfoliosite.global.entity.User;
import com.pj.portfoliosite.portfoliosite.teampost.TeamPostRepository;
import com.pj.portfoliosite.portfoliosite.teampost.dto.ReqTeamCommentDTO;
import com.pj.portfoliosite.portfoliosite.user.UserRepository;
import com.pj.portfoliosite.portfoliosite.util.PersonalDataUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamPostCommentService {
    private final TeamPostCommentRepository teamPostCommentRepository;
    private final UserRepository userRepository;
    private final TeamPostRepository teamPostRepository;
    private final PersonalDataUtil personalDataUtil;

    @Transactional
    public void addComment(Long teamPostId, ReqTeamCommentDTO reqTeamCommentDTO) {
        TeamPost teamPost = teamPostRepository.getReference(teamPostId);
        
        // 실제 로그인한 사용자 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            throw new RuntimeException("로그인이 필요합니다.");
        }
        
        String userEmail = authentication.getName();
        User user = findUserByEmailSafely(userEmail);
        
        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다: " + userEmail);
        }

        TeamPostComment teamPostComment = new TeamPostComment(
                reqTeamCommentDTO.getComment(), user, teamPost);

        if (reqTeamCommentDTO.getParentCommentId() != null) {
            TeamPostComment parentComment = teamPostCommentRepository.getReference(
                    reqTeamCommentDTO.getParentCommentId());
            teamPostComment.setParent(parentComment);
            parentComment.addReply(teamPostComment);
        }

        teamPostCommentRepository.insertComment(teamPostComment);
    }

    @Transactional
    public boolean deleteComment(Long teamPostId, Long commentId) {
        // 실제 로그인한 사용자 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            throw new RuntimeException("로그인이 필요합니다.");
        }
        
        String userEmail = authentication.getName();
        User user = findUserByEmailSafely(userEmail);
        
        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
        
        TeamPostComment teamPostComment = teamPostCommentRepository.selectByTeamPostIdAndId(
                teamPostId, commentId);
        
        if (teamPostComment != null) {
            // 본인이 작성한 댓글인지 확인
            if (!teamPostComment.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("본인이 작성한 댓글만 삭제할 수 있습니다.");
            }
            
            teamPostCommentRepository.deleteComment(teamPostComment);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean updateComment(Long teamPostId, Long commentId, String comment) {
        // 실제 로그인한 사용자 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            throw new RuntimeException("로그인이 필요합니다.");
        }
        
        String userEmail = authentication.getName();
        User user = findUserByEmailSafely(userEmail);
        
        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
        
        TeamPostComment teamPostComment = teamPostCommentRepository.selectByTeamPostIdAndId(
                teamPostId, commentId);
        
        if (teamPostComment != null) {
            // 본인이 작성한 댓글인지 확인
            if (!teamPostComment.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("본인이 작성한 댓글만 수정할 수 있습니다.");
            }
            
            teamPostComment.updateComment(comment);
            return true;
        }
        return false;
    }

    // 암호화된 이메일 처리를 위한 안전한 사용자 조회 메서드
    private User findUserByEmailSafely(String email) {
        try {
            // 1. 암호화된 이메일로 먼저 조회 시도
            try {
                String encryptedEmail = personalDataUtil.encryptPersonalData(email);
                Optional<User> userOpt = userRepository.findByEmail(encryptedEmail);
                if (userOpt.isPresent()) {
                    return userOpt.get();
                }
            } catch (Exception e) {
                // 암호화 실패 시 다음 단계로
            }

            // 2. 평문 이메일로 조회 시도
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                return userOpt.get();
            }

            // 3. 전체 사용자를 조회하여 복호화해서 비교
            List<User> allUsers = userRepository.findAllForMigration();
            for (User user : allUsers) {
                try {
                    String userEmail = user.getEmail();
                    if (userEmail != null) {
                        // 평문 이메일과 직접 비교
                        if (email.equals(userEmail)) {
                            return user;
                        }
                        
                        // 복호화해서 비교
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