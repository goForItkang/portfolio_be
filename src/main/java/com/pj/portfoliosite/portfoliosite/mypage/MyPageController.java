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
        Object obj = myPageService.getPortfolio();
        if(obj == null) {
            return ResponseEntity.notFound().build();
        }
        else{
            return ResponseEntity.ok((DataResponse) obj);
        }
    }
    @GetMapping("/blogs")
    public ResponseEntity<DataResponse> getBlogs() {
        Object object = myPageService.getBlog();
        if(object == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok((DataResponse) object);
    }
    @GetMapping("/project")
    public ResponseEntity<DataResponse> getProject(){

        Object object = myPageService.getProject();
        if(object != null){
            return ResponseEntity.ok((DataResponse) object);
        }else{
            return ResponseEntity.notFound().build();
        }

    }
    @GetMapping("/bookmark")
    public ResponseEntity<DataResponse> getBookmark(){
        Object object = myPageService.getBookMark();
        if(object != null){
            return ResponseEntity.ok((DataResponse) object);
        }else{
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/activity/like")
    public ResponseEntity<DataResponse> getWork(){
        List<ResWorkLikeDTO> resWorkLikeDTO = myPageService.getLike();
        DataResponse dataResponse = new DataResponse();
        dataResponse.setData(resWorkLikeDTO);
        if(resWorkLikeDTO.isEmpty()){
            return ResponseEntity.notFound().build();
        }else{
            return ResponseEntity.ok(dataResponse);
        }
    }
    @GetMapping("/activity/comment")
    public ResponseEntity<DataResponse> getComment(){
        ResWorkLikeDTO resWorkLikeDTO = myPageService.getComment();
        if(resWorkLikeDTO == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.notFound().build();
    }


}
