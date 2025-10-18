package com.pj.portfoliosite.portfoliosite.blog.bookmark;

import com.pj.portfoliosite.portfoliosite.blog.BlogRepository;
import com.pj.portfoliosite.portfoliosite.global.entity.Blog;
import com.pj.portfoliosite.portfoliosite.global.entity.BlogBookmark;
import com.pj.portfoliosite.portfoliosite.global.entity.User;
import com.pj.portfoliosite.portfoliosite.user.UserRepository;
import com.pj.portfoliosite.portfoliosite.util.AESUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final BlogRepository blogRepository;
    private final AESUtil aesUtil;
    public void save(Long id) {
        Blog blog = blogRepository.selectById(id);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userRepository.findByEmail(aesUtil.encode(email));
        if(user.isPresent() ){
            BlogBookmark blogBookmark = new BlogBookmark();
            blogBookmark.addUser(user.get());
            blogBookmark.addBlog(blog);
            bookmarkRepository.save(blogBookmark);
        }else{
            // 만약 사용자가 없는 경우
        }

    }

    public void delete(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userRepository.findByEmail(aesUtil.encode(email));
        if(user.isPresent()){
            bookmarkRepository.delete(id,user.get().getId());
        }else{
            return;
        }
    }

}
