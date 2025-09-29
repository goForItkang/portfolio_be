package com.pj.portfoliosite.portfoliosite.blog;

import com.pj.portfoliosite.portfoliosite.blog.bookmark.BookmarkRepository;
import com.pj.portfoliosite.portfoliosite.blog.dto.ReqBlogDTO;
import com.pj.portfoliosite.portfoliosite.blog.dto.ResBlogDTO;
import com.pj.portfoliosite.portfoliosite.blog.dto.ResBlogInfo;
import com.pj.portfoliosite.portfoliosite.blog.like.LikeRepository;
import com.pj.portfoliosite.portfoliosite.global.dto.PageDTO;
import com.pj.portfoliosite.portfoliosite.global.entity.Blog;
import com.pj.portfoliosite.portfoliosite.global.entity.User;
import com.pj.portfoliosite.portfoliosite.user.UserRepository;
import com.pj.portfoliosite.portfoliosite.util.AESUtil;
import com.pj.portfoliosite.portfoliosite.util.ImgUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlogService {
//    사용자 이메일을 받아 삭제 여부 확인 해야함
    private final BookmarkRepository bookmarkRepository;
    private final LikeRepository likeRepository;
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    private final ImgUtil imgUtil;
    private final AESUtil aesUtil;

    public void save(ReqBlogDTO reqBlogDTO) throws IOException {

        String testLogin = SecurityContextHolder.getContext().getAuthentication().getName();

        if(testLogin == null){
            throw new RuntimeException("로그인이 필요합니다. ");
        }
        String encodeEmail = aesUtil.encode(testLogin);
        Optional<User> user = userRepository.findByEmail(encodeEmail);
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
        user.get().addBlog(blog);
        blogRepository.save(blog);
    }

    public void delete(Long id) {
        String testLogin = SecurityContextHolder.getContext().getAuthentication().getName();
        if(testLogin == null){
            throw new RuntimeException("로그인이 필요합니다. ");
        }
        Optional<User> user = userRepository.findByEmail(testLogin);
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

        String testLogin = SecurityContextHolder.getContext().getAuthentication().getName();
        if(testLogin == null){
            throw new RuntimeException("로그인이 필요합니다. ");
        }
        Optional<User> user = userRepository.findByEmail(testLogin);
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
    // 추천 블로그 가져오기
    public List<ResBlogDTO> getRecommend() {
        //추천 블로그 12개 가겨오기 좋아요 순으로 12개 가져오기
        // 오늘날짜와 1주일 전날짜를 가져옴
        try{
            LocalDate today = LocalDate.now();
            LocalDate weekAgo = today.minusWeeks(1);
            List<Blog> blogs = blogRepository.selectByLikeDesc(today,weekAgo);
            return blogListToResBlogDTOList(blogs);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    private List<ResBlogDTO> blogListToResBlogDTOList(List<Blog> blogs) {
        List<ResBlogDTO> resBlogDTOS = new ArrayList<>();
        for (Blog blog : blogs) {
            ResBlogDTO resBlogDTO = new ResBlogDTO();
            resBlogDTO.setId(blog.getId());
            resBlogDTO.setTitle(blog.getTitle());
            resBlogDTO.setContent(blog.getContent());
            resBlogDTO.setWriteName(blog.getUser().getName());
            resBlogDTO.setUserId(blog.getUser().getId());
            resBlogDTO.setUserProfileURL(blog.getUser().getProfile());
            resBlogDTO.setCreatedAt(blog.getCreatedAt());
            resBlogDTO.setCategory(blog.getCategory());
            resBlogDTO.setThumbnailUrl(blog.getThumbnailURL());
            resBlogDTO.setBlogStatus(blog.getAccess()); // 변경 해야함 access에 따라
            resBlogDTOS.add(resBlogDTO);
        }
        return resBlogDTOS;

    }
    // 페이징 처리한 blogs
    public PageDTO<ResBlogDTO> getBlog(Integer page, Integer size) {
    int safePage = Math.max(page, 0);
    int safeSize = Math.min(Math.max(size, 1), 50);

    List<Blog> blogs = blogRepository.selectByCreatAtDesc(safePage,safeSize);
    Long total = blogRepository.selectCount();
    List<ResBlogDTO> content = blogListToResBlogDTOList(blogs);

    int totalPages = (int) Math.ceil(total / (double) safeSize);
    boolean first = safePage == 0;
    boolean last = (totalPages == 0) || (safePage >= totalPages - 1);
    boolean hasNext = safePage < totalPages - 1;
    boolean hasPrevious = safePage > 0;
    int count = content.size();
    return new PageDTO<>(
            content,
            safePage,
            safeSize,
            total,
            totalPages,
            first,
            last,
            hasNext,
            hasPrevious,
            count
    );
    }


}
