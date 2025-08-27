package com.pj.portfoliosite.portfoliosite.project.like;

import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProjectLikeController {
    //프로젝트 좋아요를 누른 경우 데이터 베이스에 넣어야겠죵~
    private final ProjectLikeService projectLikeService;

    @PostMapping("/project/{id}/like")
    @Operation(
            summary = "project 좋아요 누른 경우",
            description = "project 좋아요 header jwt 토큰 넣어주세용"
    )
    public ResponseEntity<DataResponse> projectLike(
            @PathVariable Long id
    ){
        DataResponse dataResponse = new DataResponse();
        dataResponse.setMessage("좋아요 성공적으로 해결했습니다");
        dataResponse.setStatus(200);
        projectLikeService.likeProject(id);
        return ResponseEntity.ok(dataResponse);
    }
    @DeleteMapping("/project/{id}/like")
    @Operation(
            summary = "project 좋아요 취소",
            description = "project 좋아요 취소 jwt 토큰 넣어주세용"
    )
    public ResponseEntity<DataResponse> deleteLike(
            @PathVariable Long id
    ){
        DataResponse dataResponse = new DataResponse();
        dataResponse.setMessage("좋아요 성공적으로 해결했습니다");
        dataResponse.setStatus(200);
        projectLikeService.likeDeleteProject(id);

        return ResponseEntity.ok(dataResponse);
    }

}

