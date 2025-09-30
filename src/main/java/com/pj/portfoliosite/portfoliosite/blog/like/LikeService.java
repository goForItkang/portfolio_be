package com.pj.portfoliosite.portfoliosite.blog.like;

import com.pj.portfoliosite.portfoliosite.blog.BlogRepository;
import com.pj.portfoliosite.portfoliosite.global.entity.Blog;
import com.pj.portfoliosite.portfoliosite.global.entity.BlogLike;
import com.pj.portfoliosite.portfoliosite.global.entity.User;
import com.pj.portfoliosite.portfoliosite.user.UserRepository;
import com.pj.portfoliosite.portfoliosite.util.AESUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    private final AESUtil aesUtil;
    public void save(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<User> user = userRepository.findByEmail(aesUtil.encode(email));
        // try catch 로 Exception 터트려야함
        if(user.isPresent()){
            Blog blog = blogRepository.selectById(id);
            BlogLike blogLike = new BlogLike();
            blogLike.addBlog(blog);
            blogLike.addUser(user.get());
            likeRepository.save(blogLike);
        }else{
            // user 가 없는경우
        }
    }

    public void delete(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userRepository.findByEmail(aesUtil.encode(email));
        // try catch 로 Exception 터트려야함
        if(user.isPresent()){
            likeRepository.deleteByBlogIdAndUserId(id,user.get().getId());
        }else{
            // user 가 없는경우
        }
    }
}
