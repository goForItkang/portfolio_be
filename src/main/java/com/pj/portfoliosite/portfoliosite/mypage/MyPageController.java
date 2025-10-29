package com.pj.portfoliosite.portfoliosite.mypage;

import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import com.pj.portfoliosite.portfoliosite.mypage.dto.ResWorkLikeDTO;
import com.pj.portfoliosite.portfoliosite.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MyPageController {
    private final MyPageService myPageService;

    @GetMapping("/portfolio")
    public ResponseEntity<DataResponse> getPortfolio() {
        DataResponse dataResponse = myPageService.getPortfolio();
        if(dataResponse.getStatus() == 401) {
            return ResponseEntity.status(401).body(dataResponse);
        }
        else if(dataResponse.getStatus() == 404) {
            return ResponseEntity.status(404).body(dataResponse);
        }
        return ResponseEntity.ok(dataResponse);
    }
    @GetMapping("/blogs")
    public ResponseEntity<DataResponse> getBlogs() {
        DataResponse dataResponse = myPageService.getBlog();
        if(dataResponse.getStatus() == 401) {
            return ResponseEntity.status(401).body(dataResponse);
        }
        else if(dataResponse.getStatus() == 404) {
            return ResponseEntity.status(404).body(dataResponse);
        }
        return ResponseEntity.ok(dataResponse);
    }
    @GetMapping("/project")
    public ResponseEntity<DataResponse> getProject(){
        DataResponse dataResponse = myPageService.getProject();
        if(dataResponse.getStatus() == 401) {
            return ResponseEntity.status(401).body(dataResponse);
        }
        else if(dataResponse.getStatus() == 404) {
            return ResponseEntity.status(404).body(dataResponse);
        }
        return ResponseEntity.ok(dataResponse);
    }
    @GetMapping("/bookmark")
    public ResponseEntity<DataResponse> getBookmark(){
        DataResponse dataResponse = myPageService.getBookMark();
        if(dataResponse.getStatus() == 401) {
            return ResponseEntity.status(401).body(dataResponse);
        }
        else if(dataResponse.getStatus() == 404) {
            return ResponseEntity.status(404).body(dataResponse);
        }
        return ResponseEntity.ok(dataResponse);
    }
    @GetMapping("/activity/like")
    public ResponseEntity<DataResponse> getWork(){
        DataResponse dataResponse = myPageService.getLike();
        if(dataResponse.getStatus() == 401) {
            return ResponseEntity.status(401).body(dataResponse);
        }
        else if(dataResponse.getStatus() == 404) {
            return ResponseEntity.status(404).body(dataResponse);
        }
        return ResponseEntity.ok(dataResponse);
    }
    @GetMapping("/activity/comment")
    public ResponseEntity<DataResponse> getComment(){
        DataResponse dataResponse = myPageService.getComment();
        if(dataResponse.getStatus() == 401) {
            return ResponseEntity.status(401).body(dataResponse);
        }
        else if(dataResponse.getStatus() == 404) {
            return ResponseEntity.status(404).body(dataResponse);
        }
        return ResponseEntity.ok(dataResponse);
    }


}
