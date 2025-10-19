package com.pj.portfoliosite.portfoliosite.mypage;

import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import com.pj.portfoliosite.portfoliosite.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MyPageController {
    private final MyPageService myPageService;

    @GetMapping("/portfolio")
    public ResponseEntity<DataResponse> getPortfolio() {
        return ResponseEntity.ok(myPageService.getPortfolio());
    }
    @GetMapping("/blogs")
    public ResponseEntity<DataResponse> getBlogs() {
        return ResponseEntity.ok(myPageService.getBlog());
    }
    @GetMapping("/project")
    public ResponseEntity<DataResponse> getProject(){
        return ResponseEntity.ok(myPageService.getProject());
    }
    @GetMapping("/bookmark")
    public ResponseEntity<DataResponse> getBookmark(){
        return ResponseEntity.ok(myPageService.getBookMark());
    }


}
