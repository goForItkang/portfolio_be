package com.pj.portfoliosite.portfoliosite.teampost.bookmark;

import com.pj.portfoliosite.portfoliosite.global.entity.TeamPost;
import com.pj.portfoliosite.portfoliosite.global.entity.TeamPostBookMark;
import com.pj.portfoliosite.portfoliosite.global.entity.User;
import com.pj.portfoliosite.portfoliosite.teampost.TeamPostRepository;
import com.pj.portfoliosite.portfoliosite.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamPostBookMarkService {
    private final TeamPostBookMarkRepository teamPostBookMarkRepository;
    private final UserRepository userRepository;
    private final TeamPostRepository teamPostRepository;

    @Transactional
    public String bookMarkTeamPost(Long id) {
        try {
            // JWT에서 사용자 정보 가져오기
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
                return "게시글을 찾을 수 없습니다.";
            }

            // 중복 북마크 확인
            if (teamPostBookMarkRepository.existBookMark(id, user.get().getId())) {
                return "이미 북마크를 추가한 게시물입니다.";
            }
            
            TeamPostBookMark teamPostBookMark = new TeamPostBookMark();
            teamPostBookMark.setTeamPost(teamPost);
            teamPostBookMark.addUser(user.get());
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
            // JWT에서 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
                return "로그인이 필요합니다.";
            }
            
            String userEmail = authentication.getName();
            Optional<User> user = userRepository.findByEmail(userEmail);
            
            if (user.isEmpty()) {
                return "사용자를 찾을 수 없습니다.";
            }

            // 북마크 존재 확인
            if (!teamPostBookMarkRepository.existBookMark(id, user.get().getId())) {
                return "북마크를 추가하지 않은 게시물입니다.";
            }
            
            teamPostBookMarkRepository.deleteBookMark(user.get().getId(), id);
            return "북마크를 취소했습니다.";
            
        } catch (Exception e) {
            return "북마크 취소 처리 중 오류가 발생했습니다.";
        }
    }
}
