package com.pj.portfoliosite.portfoliosite.blog;

import com.pj.portfoliosite.portfoliosite.blog.dto.ReqBlogDTO;
import com.pj.portfoliosite.portfoliosite.blog.dto.ResBlogDTO;
import com.pj.portfoliosite.portfoliosite.global.entity.Blog;
import com.pj.portfoliosite.portfoliosite.global.entity.User;
import com.pj.portfoliosite.portfoliosite.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlogService {
//    사용자 이메일을 받아 삭제 여부 확인 해야함

    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    public void save(ReqBlogDTO reqBlogDTO) {
        String userEmail = "portclod.com";
        Optional<User> user =userRepository.findByEmail(userEmail);
        Blog blog = new Blog();
        blog.blogSave(reqBlogDTO);
        blog.addUser(user.get());
        blogRepository.save(blog);
    }

    public void delete(Long id) {
        String userEmail = "portclod.com"; // 사용자 이메일
        Optional<User> user =userRepository.findByEmail(userEmail);
        Blog blog = blogRepository.selectById(id);
        if(user.isPresent() && blog.getUser().equals(user.get())){
            // exceptio 처리
        }
        blogRepository.delete(blog);
    }

    public ResBlogDTO getId(Long id) {

        return null;
    }
    @Transactional
    public void update(Long id, ReqBlogDTO reqBlogDTO) {
        Blog blog = blogRepository.selectById(id);
        blog.blogSave(reqBlogDTO);
    }
}
