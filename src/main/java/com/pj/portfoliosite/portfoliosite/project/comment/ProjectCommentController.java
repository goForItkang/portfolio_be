package com.pj.portfoliosite.portfoliosite.project.comment;

import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import com.pj.portfoliosite.portfoliosite.global.dto.ReqCommentDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
