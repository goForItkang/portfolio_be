package com.pj.portfoliosite.portfoliosite.blog.comment;

import com.pj.portfoliosite.portfoliosite.blog.BlogRepository;
import com.pj.portfoliosite.portfoliosite.blog.dto.ReqBlogCommentDTO;
import com.pj.portfoliosite.portfoliosite.blog.dto.ResBlogComment;
import com.pj.portfoliosite.portfoliosite.global.entity.Blog;
import com.pj.portfoliosite.portfoliosite.global.entity.BlogComment;
import com.pj.portfoliosite.portfoliosite.global.entity.User;
import com.pj.portfoliosite.portfoliosite.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BlogRepository blogRepository;
    // 저장 메서드
    public void save(Long blogId, ReqBlogCommentDTO req) {
        Blog blog = blogRepository.selectById(blogId);
        String loginEmail = "portcloud@naver.com";
        Optional<User> user =  userRepository.findByEmail(loginEmail);
        if(req.getParentCommentId() == null ){
            // 부모가 없는 댓글 일 경우 즉 대댓글이 없는 경우
            BlogComment comment = new BlogComment();
            comment.commentSave(req.getComment());
            comment.addUser(user.get());
            comment.addBlog(blog);
            commentRepository.save(comment);
        }else{
            Long id = req.getParentCommentId();
            // 부모 댓글 가져옴
            BlogComment blogComment = commentRepository.selectById(id);
            // 대 댓글 작성부분
            BlogComment blogCommentReply = new BlogComment();
            blogCommentReply.addUser(user.get());
            blogCommentReply.addBlog(blog);
            blogCommentReply.setParent(blogComment);
            commentRepository.save(blogCommentReply);
        }
    }
    // 블로그 Id 로 comment Lst 가져오는 경우


    public List<ResBlogComment> getComment(Long id) {
        // 접속 하는 사용자(없을 수 있음)
        String userEmail = "portfolio@naver.com";
        Optional<User> currentUser = userRepository.findByEmail(userEmail);
        Long currentUserId = currentUser.map(User::getId).orElse(null);

        List<BlogComment> blogComments = commentRepository.selectByBlogId(id);
        // 전송 할 데이터: 부모(최상위) 댓글만 선별
        List<ResBlogComment> result = new ArrayList<>();
        for (BlogComment blogComment : blogComments) {
            if (blogComment.getParent() == null) {
                result.add(toDTO(blogComment, currentUserId));
            }
        }
        return result;
    }

    // 대댓글을 포함해 재귀적으로 DTO 변환 + isOwner 설정
    private ResBlogComment toDTO(BlogComment comment, Long currentUserId) {
        ResBlogComment dto = new ResBlogComment();
        dto.setId(comment.getId());
        dto.setComment(comment.getComment());
        Long authorId = (comment.getUser() != null) ? comment.getUser().getId() : null;
        dto.setUserId(authorId);

        // isOwner: 미로그인(null), 로그인했지만 본인 아님(false), 본인(true)
        if (currentUserId == null) {
            dto.setOwner(false);
        } else {
            dto.setOwner(authorId != null && authorId.equals(currentUserId));
        }

        // 자식(대댓글) 재귀 변환
        List<ResBlogComment> childDTOs = new ArrayList<>();
        if (comment.getReplies() != null) {
            for (BlogComment reply : comment.getReplies()) {
                childDTOs.add(toDTO(reply, currentUserId));
            }
        }
        dto.setReplies(childDTOs);
        return dto;
    }




}
