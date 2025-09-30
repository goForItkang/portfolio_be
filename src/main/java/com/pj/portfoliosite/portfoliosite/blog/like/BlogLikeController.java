package com.pj.portfoliosite.portfoliosite.blog.like;

import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.crypto.Data;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BlogLikeController {
     private final LikeService likeService;

//     테스트 완료
    @PostMapping("/blogs/{id}/likes")
    public ResponseEntity<DataResponse> blogLike(
            @PathVariable Long id
    ){

        DataResponse dataResponse = new DataResponse();
        dataResponse.setMessage("블로그 좋아요 성공적으로 저장했습니다");
        dataResponse.setStatus(200);
        likeService.save(id);
        return ResponseEntity.ok(dataResponse);
    }
//    테스트 완료
    @DeleteMapping("/blogs/{id}/likes")
    public ResponseEntity<DataResponse> blogLikeDelete(
            @PathVariable Long id
    ){
        DataResponse dataResponse = new DataResponse();
        likeService.delete(id);
        dataResponse.setMessage("블로그 좋아요 성공적으로 제거했습니다");
        dataResponse.setStatus(200);
        return ResponseEntity.ok(dataResponse);
    }
}
