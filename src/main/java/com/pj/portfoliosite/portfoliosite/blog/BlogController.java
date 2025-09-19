package com.pj.portfoliosite.portfoliosite.blog;

import com.pj.portfoliosite.portfoliosite.blog.dto.ReqBlogDTO;
import com.pj.portfoliosite.portfoliosite.blog.dto.ResBlogDTO;
import com.pj.portfoliosite.portfoliosite.blog.dto.ResBlogInfo;
import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BlogController {
    private final BlogService blogService;
//    테스트 완료
    @PostMapping(value = "/blogs",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "블로그 저장"
    )
    public ResponseEntity<DataResponse> blogUpload(
            ReqBlogDTO reqBlogDTO
    ) throws IOException {

        blogService.save(reqBlogDTO);
        DataResponse dataResponse = new DataResponse();
        dataResponse.setMessage("성공적으로 등록 되었습니다.");
        dataResponse.setStatus(200);
        return ResponseEntity.ok(dataResponse);
    }
//  테스트 완료
    @DeleteMapping("/blogs/{id}")
    public ResponseEntity<DataResponse> deleteBlog(
            @PathVariable Long id
    ){
        blogService.delete(id);
        DataResponse dataResponse = new DataResponse();
        dataResponse.setMessage("정상적으로 삭제 되었습니다.");
        dataResponse.setStatus(200);
        return ResponseEntity.ok(dataResponse);
    }
//  테스트 완료
    @GetMapping("/blogs/{id}")
    public ResponseEntity<DataResponse> getBlogs(
            @PathVariable Long id
    ){
        ResBlogDTO resBlogDTO =  blogService.getId(id);
        DataResponse dataResponse = new DataResponse();
        dataResponse.setStatus(200);
        dataResponse.setData(resBlogDTO);
        return ResponseEntity.ok(dataResponse);

    }
//    테스트완료
    @PatchMapping(value = "/blogs/{id}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DataResponse> updateBlog(
            @PathVariable Long id,
            ReqBlogDTO reqBlogDTO
    ) throws IOException {

        blogService.update(id,reqBlogDTO);
        DataResponse dataResponse = new DataResponse();
        dataResponse.setMessage("정상적으로 수정 되었습니다.");
        return ResponseEntity.ok(dataResponse);
    }
//    테스트 완료
    @GetMapping("/blogs/{id}/info")
    @Operation(
            summary = "북마크 및 좋아요 count and owner check"
    )
    public ResponseEntity<DataResponse> getBlogInfo(
            @PathVariable Long id
    ){
        ResBlogInfo resBlogInfo =  blogService.getInfo(id);
        DataResponse dataResponse = new DataResponse();
        dataResponse.setStatus(200);
        dataResponse.setData(resBlogInfo);
        return ResponseEntity.ok(dataResponse);
    }

}

