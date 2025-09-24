package com.pj.portfoliosite.portfoliosite.teampost.bookmark;

import com.pj.portfoliosite.portfoliosite.global.entity.TeamPost;
import com.pj.portfoliosite.portfoliosite.global.entity.TeamPostBookMark;
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
public class TeamPostBookMarkService {
    private final TeamPostBookMarkRepository teamPostBookMarkRepository;
    private final UserRepository userRepository;
    private final TeamPostRepository teamPostRepository;
    private final PersonalDataUtil personalDataUtil;

    @Transactional
    public String bookMarkTeamPost(Long id) {
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
                return "게시글을 찾을 수 없습니다.";
            }

            if (teamPostBookMarkRepository.existBookMark(id, user.getId())) {
                return "이미 북마크를 추가한 게시물입니다.";
            }
            
            TeamPostBookMark teamPostBookMark = new TeamPostBookMark();
            teamPostBookMark.setTeamPost(teamPost);
            teamPostBookMark.addUser(user);
            teamPostBookMarkRepository.insertBookMark(teamPostBookMark);
            teamPost.addBookMark(teamPostBookMark);
            
            return "북마크를 추가했습니다.";
            
        } catch (Exception e) {
            return "북마크 처리 중 오류가 발생했습니다.";
        }
    }

    @Transactional
    public String bookMarkDeleteTeamPost(Long id) {
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

            if (!teamPostBookMarkRepository.existBookMark(id, user.getId())) {
                return "북마크를 추가하지 않은 게시물입니다.";
            }
            
            teamPostBookMarkRepository.deleteBookMark(user.getId(), id);
            return "북마크를 취소했습니다.";
            
        } catch (Exception e) {
            return "북마크 취소 처리 중 오류가 발생했습니다.";
        }
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
                // 무시
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
                            // 무시
                        }
                    }
                } catch (Exception e) {
                    // 무시
                }
            }
            
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
