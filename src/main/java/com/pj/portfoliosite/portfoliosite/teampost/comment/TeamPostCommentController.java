package com.pj.portfoliosite.portfoliosite.teampost.comment;

import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import com.pj.portfoliosite.portfoliosite.teampost.dto.ReqTeamCommentDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TeamPostCommentController {
    private final TeamPostCommentService teamPostCommentService;

    @GetMapping("/teampost/{teamPostId}/comments")
    @Operation(summary = "댓글 조회", description = "특정 게시글의 모든 댓글 조회 (비로그인 가능)")
    public ResponseEntity<DataResponse> getComments(@PathVariable Long teamPostId) {
        DataResponse dataResponse = new DataResponse();
        dataResponse.setData(teamPostCommentService.getComments(teamPostId));
        dataResponse.setMessage("댓글 조회 성공");
        return ResponseEntity.ok(dataResponse);
    }

    @PostMapping("/teampost/{teamPostId}/comments")
    @Operation(summary = "댓글 작성", description = "header에 JWT 토큰 필요")
    public ResponseEntity<DataResponse> addComment(
            @PathVariable Long teamPostId,
            @RequestBody ReqTeamCommentDTO reqTeamCommentDTO) {
        teamPostCommentService.addComment(teamPostId, reqTeamCommentDTO);
        DataResponse dataResponse = new DataResponse();
        dataResponse.setMessage("댓글 작성 완료");
        return ResponseEntity.ok(dataResponse);
    }

    @DeleteMapping("/teampost/{teamPostId}/comments/{commentId}")
    @Operation(summary = "댓글 삭제", description = "header에 JWT 토큰 필요 (본인 댓글만 삭제 가능)")
    public ResponseEntity<DataResponse> deleteComment(
            @PathVariable Long teamPostId,
            @PathVariable Long commentId) {
        boolean result = teamPostCommentService.deleteComment(teamPostId, commentId);
        DataResponse dataResponse = new DataResponse();
        if (result) {
            dataResponse.setMessage("댓글 삭제 완료");
        } else {
            dataResponse.setMessage("댓글을 찾을 수 없습니다");
        }
        return ResponseEntity.ok(dataResponse);
    }

    @PatchMapping("/teampost/{teamPostId}/comments/{commentId}")
    @Operation(summary = "댓글 수정", description = "header에 JWT 토큰 필요 (본인 댓글만 수정 가능)")
    public ResponseEntity<DataResponse> updateComment(
            @PathVariable Long teamPostId,
            @PathVariable Long commentId,
            @RequestParam String comment) {
        boolean result = teamPostCommentService.updateComment(teamPostId, commentId, comment);
        DataResponse dataResponse = new DataResponse();
        if (result) {
            dataResponse.setMessage("댓글 수정 완료");
        } else {
            dataResponse.setMessage("댓글 수정 실패");
        }
        return ResponseEntity.ok(dataResponse);
    }
}