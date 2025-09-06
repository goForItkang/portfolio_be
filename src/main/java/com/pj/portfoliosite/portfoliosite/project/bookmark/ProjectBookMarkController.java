package com.pj.portfoliosite.portfoliosite.project.bookmark;

import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import com.pj.portfoliosite.portfoliosite.global.entity.ProjectBookMark;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProjectBookMarkController {
    private final ProjectBookMarkService projectBookMarkService;

    @PostMapping("/project/{id}/bookmark")
    @Operation(
            summary = "project bookMark 누른 경우",
            description = "project bookmark hearder에 jwt 토큰 넣어주세용~"
    )
    public ResponseEntity<DataResponse> projectBookMark(
            @PathVariable("id") Long id){
        projectBookMarkService.bookMarkProject(id);
        DataResponse dataResponse = new DataResponse();
        dataResponse.setMessage("success");
        return ResponseEntity.ok(dataResponse);
    }
    @DeleteMapping("/project/{id}/bookmark")
    @Operation(
            summary = "project bookMark 누른 경우",
            description = "project bookmark hearder에 jwt 토큰 넣어주세용~"
    )
    public ResponseEntity<ProjectBookMark> projectDeleteBookMark(
            @PathVariable("id") Long id){
        projectBookMarkService.bookMarkDeleteProject(id);
        return null;
    }

}
