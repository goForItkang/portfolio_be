package com.pj.portfoliosite.portfoliosite.blog.comment;

import com.pj.portfoliosite.portfoliosite.blog.dto.ReqBlogCommentDTO;
import com.pj.portfoliosite.portfoliosite.blog.dto.ResBlogComment;
import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {
    private final CommentService commentService;
    @GetMapping("/blogs/{id}/comments")
   public ResponseEntity<DataResponse> getComment(
           @PathVariable Long id
   ){
        List<ResBlogComment> resBlogCommentList = commentService.getComment(id);
        DataResponse dataResponse = new DataResponse();
        dataResponse.setData(resBlogCommentList);
        return ResponseEntity.ok(dataResponse);
   }
//   테스트 중
   @PostMapping("/blogs/{id}/comments")
    public ResponseEntity<DataResponse> saveComment(
            @PathVariable Long id,
            @RequestBody ReqBlogCommentDTO req
   ){
        DataResponse dataResponse = new DataResponse();
        dataResponse.setMessage("블로그 댓글이 성공적으로 작성 되었습니다.");
        dataResponse.setStatus(200);
        commentService.save(id,req);
       return ResponseEntity.ok(dataResponse);
   }
   @DeleteMapping("/blogs/{blogsId}/comments/{commentId}")
    public ResponseEntity<DataResponse> deleteComment(
            @PathVariable Long blogsId,
            @PathVariable Long commentId
   ){
        commentService.delete(blogsId,commentId);
        return null;
   }
   @PatchMapping("/blogs/{id}/comment")
    public ResponseEntity<DataResponse> updateComment(
            @PathVariable Long id,
            @RequestBody ReqBlogCommentDTO req
   ){
        DataResponse dataResponse = new DataResponse();
        dataResponse.setMessage("성공적으로 변경하겠습니다");
        commentService.update(id,req);
        return ResponseEntity.ok(dataResponse);
   }
}
