package com.pj.portfoliosite.portfoliosite.blog;

import com.pj.portfoliosite.portfoliosite.blog.bookmark.BookmarkRepository;
import com.pj.portfoliosite.portfoliosite.blog.bookmark.BookmarkService;
import com.pj.portfoliosite.portfoliosite.blog.comment.CommentService;
import com.pj.portfoliosite.portfoliosite.blog.dto.ReqBlogDTO;
import com.pj.portfoliosite.portfoliosite.blog.dto.ResBlogDTO;
import com.pj.portfoliosite.portfoliosite.blog.dto.ResBlogInfo;
import com.pj.portfoliosite.portfoliosite.blog.like.LikeRepository;
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
    private final BookmarkRepository bookmarkRepository;
    private final LikeRepository likeRepository;
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

    public ResBlogInfo getInfo(Long id) {
        String loginEmail = "portfolio@naver.com";
        Optional<User> user = userRepository.findByEmail(loginEmail);
        ResBlogInfo resBlogInfo = new ResBlogInfo();
        if(user.isPresent()){
            // 사용자 있으면
            resBlogInfo = blogRepository.selectBlogInfoByBlogAndUserId(id,user.get().getId());

        }else{
            // 사용자가 없을경우
            resBlogInfo = blogRepository.selectBlogInfoByBlogId(id);
            resBlogInfo.setBookMarkCheck(false);
            resBlogInfo.setLikeCheck(false);
        }
        return resBlogInfo;
    }

}
