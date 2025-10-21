package com.pj.portfoliosite.portfoliosite.teampost.comment;

import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import com.pj.portfoliosite.portfoliosite.teampost.dto.ReqTeamCommentDTO;
import com.pj.portfoliosite.portfoliosite.teampost.dto.ResTeamCommentListDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class TeamPostCommentController {
    private final TeamPostCommentService teamPostCommentService;

    @GetMapping("/teampost/{teamPostId}/comments")
    public ResponseEntity<DataResponse> getComments(@PathVariable Long teamPostId) {
        List<ResTeamCommentListDTO> comments = teamPostCommentService.getComments(teamPostId);
        DataResponse dataResponse = new DataResponse();
        dataResponse.setData(comments);
        return ResponseEntity.ok(dataResponse);
    }

    @PostMapping("/teampost/{teamPostId}/comments")
    public ResponseEntity<DataResponse> addComment(
            @PathVariable Long teamPostId,
            @RequestBody ReqTeamCommentDTO reqTeamCommentDTO) {
        log.info("팀포스트 댓글 정보 {}",reqTeamCommentDTO.getParentCommentId());
        teamPostCommentService.addComment(teamPostId, reqTeamCommentDTO);
        DataResponse dataResponse = new DataResponse();
        dataResponse.setMessage("댓글 작성 완료");
        return ResponseEntity.ok(dataResponse);
    }

    @DeleteMapping("/teampost/{teamPostId}/comments/{commentId}")
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