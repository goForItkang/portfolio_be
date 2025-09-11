package com.pj.portfoliosite.portfoliosite.blog.bookmark;

import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BookmarkController {
    private final BookmarkService bookmarkService;
    @PostMapping("/blogs/{id}/bookmarks")
    public ResponseEntity<DataResponse> blogBookmark(
            @PathVariable Long id
    ){

        DataResponse dataResponse = new DataResponse();
        dataResponse.setMessage("블로그 좋아요 성공적으로 저장했습니다");
        dataResponse.setStatus(200);
        bookmarkService.save(id);
        return ResponseEntity.ok(dataResponse);
    }

    @DeleteMapping("/blogs/{id}/bookmarks")
    public ResponseEntity<DataResponse> blogBookmarkDelete(
            @PathVariable Long id
    ){
        DataResponse dataResponse = new DataResponse();
        bookmarkService.delete(id);
        dataResponse.setMessage("블로그 좋아요 성공적으로 제거했습니다");
        dataResponse.setStatus(200);
        return ResponseEntity.ok(dataResponse);
    }
}
