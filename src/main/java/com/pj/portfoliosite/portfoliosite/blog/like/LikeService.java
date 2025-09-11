package com.pj.portfoliosite.portfoliosite.blog.like;

import com.pj.portfoliosite.portfoliosite.blog.BlogRepository;
import com.pj.portfoliosite.portfoliosite.global.entity.User;
import com.pj.portfoliosite.portfoliosite.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    public void save(Long id) {
        String userEmail = "";
        Optional<User> user = userRepository.findByEmail(userEmail);
        // try catch 로 Exception 터트려야함
        if(user.isPresent()){

        }//
    }
}
