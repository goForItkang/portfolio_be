package com.pj.portfoliosite.portfoliosite.project.bookmark;

import com.pj.portfoliosite.portfoliosite.global.entity.ProjectBookMark;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProjectBookMarkController {
    private final ProjectBookMarkService projectBookMarkService;

    @PostMapping("/project/{id}/bookmakr")
    @Operation(
            summary = "project bookMark 누른 경우",
            description = "project bookmark hearder에 jwt 토큰 넣어주세용~"
    )
    public ResponseEntity<ProjectBookMark> projectBookMark(
            @PathVariable("id") Long id){
        projectBookMarkService.bookMarkProject(id);
        return null;
    }
    @DeleteMapping("/project/{id}/bookmakr")
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
