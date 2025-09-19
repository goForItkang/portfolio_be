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
import com.pj.portfoliosite.portfoliosite.util.ImgUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlogService {
//    사용자 이메일을 받아 삭제 여부 확인 해야함
    private final BookmarkRepository bookmarkRepository;
    private final LikeRepository likeRepository;
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    private final ImgUtil imgUtil;
    public void save(ReqBlogDTO reqBlogDTO) throws IOException {

        String userEmail = "portfolio@naver.com";
        Optional<User> user =userRepository.findByEmail(userEmail);
        Blog blog = new Blog();
        // 이미지 등록
        if(reqBlogDTO.getThumbnail() != null){
            String imgUrl = imgUtil.imgUpload(reqBlogDTO.getThumbnail());
            blog.setImgURL(imgUrl);
        }else{
            blog.setImgURL(null);
        }
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
    // 블로그 가벼오기 (id)
    public ResBlogDTO getId(Long id) {
        Blog blog = blogRepository.selectById(id);
        ResBlogDTO resBlogDTO = new ResBlogDTO();

        String userEmail = "portfolio@naver.com";
        Optional<User> user =userRepository.findByEmail(userEmail);
        if(user.isPresent()){
            // 로그인 한 사용자 일 경우
            if(user.get().getId() == blog.getUser().getId()){
                resBlogDTO.setOwner(true);
            }else{
                resBlogDTO.setOwner(false);
            }
        }else{
            resBlogDTO.setOwner(false);
        }

        resBlogDTO.setId(blog.getId());
        resBlogDTO.setTitle(blog.getTitle());
        resBlogDTO.setContent(blog.getContent());
        resBlogDTO.setCategory(blog.getCategory());
        resBlogDTO.setThumbnailUrl(blog.getThumbnailURL());
        resBlogDTO.setBlogStatus(blog.getAccess()); // 접근 파일
        resBlogDTO.setCreatedAt(blog.getCreatedAt());
        resBlogDTO.setWriteName(blog.getUser().getName());
        resBlogDTO.setUserId(blog.getUser().getId());
        resBlogDTO.setUserProfileURL(blog.getUser().getProfile());

        return resBlogDTO;
    }
    //수정
    @Transactional
    public void update(Long id, ReqBlogDTO reqBlogDTO) throws IOException {
        Blog blog = blogRepository.selectById(id);
        if(reqBlogDTO.getThumbnail() != null){
            String imgUrl = imgUtil.imgUpload(reqBlogDTO.getThumbnail());
            blog.setImgURL(imgUrl);
            blog.update(reqBlogDTO);
        }else{
            blog.update(reqBlogDTO);
        }
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
