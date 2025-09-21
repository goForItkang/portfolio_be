package com.pj.portfoliosite.portfoliosite.teampost.like;

import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TeamPostLikeController {
    private final TeamPostLikeService teamPostLikeService;

    @PostMapping("/teampost/{id}/like")
    @Operation(summary = "팀포스트 좋아요", description = "JWT 토큰 필요")
    public ResponseEntity<DataResponse> likeTeamPost(@PathVariable Long id) {
        String result = teamPostLikeService.likeTeamPost(id);
        DataResponse dataResponse = new DataResponse();
        dataResponse.setMessage(result);
        
        if (result.contains("이미")) {
            dataResponse.setStatus(409); // Conflict
        } else if (result.contains("로그인이 필요")) {
            dataResponse.setStatus(401); // Unauthorized
        } else if (result.contains("찾을 수 없습니다") || result.contains("오류")) {
            dataResponse.setStatus(400); // Bad Request
        } else {
            dataResponse.setStatus(200); // Success
        }
        
        return ResponseEntity.ok(dataResponse);
    }

    @DeleteMapping("/teampost/{id}/like")
    @Operation(summary = "팀포스트 좋아요 취소", description = "JWT 토큰 필요")
    public ResponseEntity<DataResponse> deleteLike(@PathVariable Long id) {
        String result = teamPostLikeService.likeDeleteTeamPost(id);
        DataResponse dataResponse = new DataResponse();
        dataResponse.setMessage(result);
        
        if (result.contains("누르지 않은")) {
            dataResponse.setStatus(404); // Not Found
        } else if (result.contains("로그인이 필요")) {
            dataResponse.setStatus(401); // Unauthorized
        } else if (result.contains("찾을 수 없습니다") || result.contains("오류")) {
            dataResponse.setStatus(400); // Bad Request
        } else {
            dataResponse.setStatus(200); // Success
        }
        
        return ResponseEntity.ok(dataResponse);
    }
}
