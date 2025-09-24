package com.pj.portfoliosite.portfoliosite.teampost.like;

import com.pj.portfoliosite.portfoliosite.global.entity.TeamPost;
import com.pj.portfoliosite.portfoliosite.global.entity.TeamPostLike;
import com.pj.portfoliosite.portfoliosite.global.entity.User;
import com.pj.portfoliosite.portfoliosite.teampost.TeamPostRepository;
import com.pj.portfoliosite.portfoliosite.user.UserRepository;
import com.pj.portfoliosite.portfoliosite.util.PersonalDataUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamPostLikeService {
    private final TeamPostLikeRepository teamPostLikeRepository;
    private final UserRepository userRepository;
    private final TeamPostRepository teamPostRepository;
    private final PersonalDataUtil personalDataUtil;

    @Transactional
    public String likeTeamPost(Long id) {
        try {
            // JWT에서 사용자 정보 가져오기
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
                return "게시글을 찾을 수 없습니다.";
            }

            // 중복 좋아요 확인
            if (teamPostLikeRepository.existLike(id, user.getId())) {
                return "이미 좋아요를 누른 게시물입니다.";
            }
            
            TeamPostLike teamPostLike = new TeamPostLike();
            teamPostLike.setTeamPost(teamPost);
            teamPostLike.addUser(user);
            teamPostLikeRepository.insertLike(teamPostLike);
            teamPost.addLike(teamPostLike);
            
            return "좋아요를 눌렀습니다.";
            
        } catch (Exception e) {
            return "좋아요 처리 중 오류가 발생했습니다.";
        }
    }

    @Transactional
    public String likeDeleteTeamPost(Long id) {
        try {
            // JWT에서 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
                return "로그인이 필요합니다.";
            }
            
            String userEmail = authentication.getName();
            User user = findUserByEmailSafely(userEmail);
            
            if (user == null) {
                return "사용자를 찾을 수 없습니다.";
            }

            // 좋아요 존재 확인
            if (!teamPostLikeRepository.existLike(id, user.getId())) {
                return "좋아요를 누르지 않은 게시물입니다.";
            }
            
            teamPostLikeRepository.deleteLike(user.getId(), id);
            return "좋아요를 취소했습니다.";
            
        } catch (Exception e) {
            return "좋아요 취소 처리 중 오류가 발생했습니다.";
        }
    }
    
    /**
     * 안전한 사용자 검색 (평문/암호화 모두 지원)
     */
    private User findUserByEmailSafely(String email) {
        try {
            log.debug("좋아요 서비스 - 사용자 검색 시작: {}", personalDataUtil.maskEmail(email));
            
            // 1단계: 암호화된 이메일로 직접 검색
            try {
                String encryptedEmail = personalDataUtil.encryptPersonalData(email);
                Optional<User> userOpt = userRepository.findByEmail(encryptedEmail);
                if (userOpt.isPresent()) {
                    log.debug("암호화된 이메일로 사용자 발견");
                    return userOpt.get();
                }
            } catch (Exception e) {
                log.debug("암호화된 이메일 검색 실패: {}", e.getMessage());
            }

            // 2단계: 평문 이메일로 검색
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                log.debug("평문 이메일로 사용자 발견");
                return userOpt.get();
            }

            // 3단계: 전체 사용자 대상 안전한 비교
            List<User> allUsers = userRepository.findAllForMigration();
            for (User user : allUsers) {
                try {
                    String userEmail = user.getEmail();
                    if (userEmail != null) {
                        // 평문 비교
                        if (email.equals(userEmail)) {
                            log.debug("평문 비교로 사용자 발견");
                            return user;
                        }
                        
                        // 복호화 비교
                        try {
                            String decryptedEmail = personalDataUtil.decryptPersonalData(userEmail);
                            if (email.equals(decryptedEmail)) {
                                log.debug("복호화 비교로 사용자 발견");
                                return user;
                            }
                        } catch (Exception decryptError) {
                            // 복호화 실패는 무시하고 계속
                        }
                    }
                } catch (Exception e) {
                    // 개별 사용자 처리 실패 무시
                }
            }
            
            log.warn("좋아요 서비스 - 사용자를 찾을 수 없음: {}", personalDataUtil.maskEmail(email));
            return null;
            
        } catch (Exception e) {
            log.error("좋아요 서비스 - 사용자 검색 중 치명적 오류: {}", e.getMessage());
            return null;
        }
    }
}
