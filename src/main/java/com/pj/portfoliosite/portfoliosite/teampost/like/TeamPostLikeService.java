package com.pj.portfoliosite.portfoliosite.teampost.like;

import com.pj.portfoliosite.portfoliosite.global.entity.TeamPost;
import com.pj.portfoliosite.portfoliosite.global.entity.TeamPostLike;
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
public class TeamPostLikeService {
    private final TeamPostLikeRepository teamPostLikeRepository;
    private final UserRepository userRepository;
    private final TeamPostRepository teamPostRepository;

    @Transactional
    public String likeTeamPost(Long id) {
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

            // 중복 좋아요 확인
            if (teamPostLikeRepository.existLike(id, user.get().getId())) {
                return "이미 좋아요를 누른 게시물입니다.";
            }
            
            TeamPostLike teamPostLike = new TeamPostLike();
            teamPostLike.setTeamPost(teamPost);
            teamPostLike.addUser(user.get());
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
            Optional<User> user = userRepository.findByEmail(userEmail);
            
            if (user.isEmpty()) {
                return "사용자를 찾을 수 없습니다.";
            }

            // 좋아요 존재 확인
            if (!teamPostLikeRepository.existLike(id, user.get().getId())) {
                return "좋아요를 누르지 않은 게시물입니다.";
            }
            
            teamPostLikeRepository.deleteLike(user.get().getId(), id);
            return "좋아요를 취소했습니다.";
            
        } catch (Exception e) {
            return "좋아요 취소 처리 중 오류가 발생했습니다.";
        }
    }
}
