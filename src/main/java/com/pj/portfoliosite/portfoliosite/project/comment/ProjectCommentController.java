package com.pj.portfoliosite.portfoliosite.project.comment;

import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import com.pj.portfoliosite.portfoliosite.global.dto.ReqCommentDTO;
import com.pj.portfoliosite.portfoliosite.global.dto.ResCommentListDTO;
import com.pj.portfoliosite.portfoliosite.global.dto.ResCommentsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProjectCommentController {
    private final ProjectCommentService projectCommentService;
    // comment 작성
    @PostMapping("/project/{projectId}/comments")
    public ResponseEntity<DataResponse> addComment(
            @PathVariable Long projectId,
            @RequestBody ReqCommentDTO reqCommentDTO
    ){
        projectCommentService.addComment(projectId,reqCommentDTO);
        DataResponse dataResponse = new DataResponse();
        dataResponse.setMessage("저장 성공");

        return ResponseEntity.ok(
            dataResponse
        );
    }
    @DeleteMapping("/project/{projectId}/comments/{commentId}")
    public ResponseEntity<DataResponse> deleteComment(
            @PathVariable Long projectId,
            @PathVariable Long commentId
    ){
        boolean result = projectCommentService.deleteComment(projectId,commentId);
        DataResponse dataResponse = new DataResponse();
        if(result){
            dataResponse.setMessage("정상적으로 삭제됨");
            return ResponseEntity.ok(dataResponse);
        }
        dataResponse.setMessage("댓글을 찾아 볼수 없습니다");
        return ResponseEntity.ok(dataResponse);
    }
    @PatchMapping("/project/{projectId}/comments/{commentId}")
    public ResponseEntity<DataResponse> updateComment(
            @PathVariable Long projectId,
            @PathVariable Long commentId,
            @RequestParam String comment
    ){
        boolean result = projectCommentService.updateComment(projectId,commentId,comment);
        DataResponse dataResponse = new DataResponse();
        if(result) {
            dataResponse.setMessage("정상적으로 수정됨");
            return ResponseEntity.ok(dataResponse);
        }
        dataResponse.setMessage("댓글찾지 못하거나 수정 실패");
        return ResponseEntity.ok(dataResponse);
    }
    @GetMapping("/project/{projectId}/comment")
    public ResponseEntity<DataResponse> getComment(
            @PathVariable Long projectId
    ){
        List<ResCommentListDTO> resCommentListDTO = projectCommentService.getComment(projectId);
        DataResponse dataResponse = new DataResponse();
        dataResponse.setData(resCommentListDTO);
        return ResponseEntity.ok(dataResponse);
    }

}
