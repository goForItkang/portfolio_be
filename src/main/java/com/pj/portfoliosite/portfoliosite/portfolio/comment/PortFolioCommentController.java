package com.pj.portfoliosite.portfoliosite.portfolio.comment;

import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import com.pj.portfoliosite.portfoliosite.global.dto.ReqCommentDTO;
import com.pj.portfoliosite.portfoliosite.global.dto.ResCommentListDTO;
import com.pj.portfoliosite.portfoliosite.global.dto.ResCommentsDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PortFolioCommentController {
    private final PortFolioCommentService portfolioCommentService;
    @PostMapping("/portfolio/{id}/comment")
    @Operation(
            summary = "포트폴리오 댓글 추가",
            description = "댓글 추가 jwt 토큰 추가 해주세요"

    )
    public ResponseEntity<DataResponse> addComment(
            @PathVariable Long id,
            @RequestBody ReqCommentDTO reqCommentDTO
    ){
        portfolioCommentService.saveComment(id, reqCommentDTO);
        DataResponse dataResponse = new DataResponse();
        dataResponse.setMessage("저장 성공");
        dataResponse.setData(null);
        return ResponseEntity.ok(dataResponse);
    }
    @GetMapping("/portfolio/{id}/comment")
    public ResponseEntity<DataResponse> getComments(
            @PathVariable Long id
    ){
        DataResponse dataResponse = new DataResponse();
        List<ResCommentListDTO> resCommentsDTO = portfolioCommentService.getComment(id);
        dataResponse.setData(resCommentsDTO);
        return ResponseEntity.ok(dataResponse);
    }

    @DeleteMapping("/portfolio/{id}/comments/{commentId}")
    public ResponseEntity<DataResponse> deleteComment(
            @PathVariable Long id,
            @PathVariable Long commentId
    ) {
        boolean result = portfolioCommentService.deleteComment(id, commentId);
        DataResponse resp = new DataResponse();
        if (result) {
            resp.setMessage("정상적으로 삭제됨");
        } else {
            resp.setMessage("댓글을 찾아 볼 수 없습니다");
        }
        return ResponseEntity.ok(resp);
    }

    @PatchMapping("/portfolio/{id}/comments/{commentId}")
    public ResponseEntity<DataResponse> updateComment(
            @PathVariable Long id,
            @PathVariable Long commentId,
            @RequestParam String comment
    ) {
        boolean result = portfolioCommentService.updateComment(id, commentId, comment);
        DataResponse resp = new DataResponse();
        if (result) {
            resp.setMessage("정상적으로 수정됨");
        } else {
            resp.setMessage("댓글을 찾지 못하거나 수정 실패");
        }
        return ResponseEntity.ok(resp);
    }
}
