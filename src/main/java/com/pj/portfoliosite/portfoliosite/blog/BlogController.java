package com.pj.portfoliosite.portfoliosite.blog;

import com.pj.portfoliosite.portfoliosite.blog.dto.ReqBlogDTO;
import com.pj.portfoliosite.portfoliosite.blog.dto.ResBlogDTO;
import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BlogController {
    private final BlogService blogService;
    @PostMapping("/blogs")
    @Operation(
            summary = "프로젝트 등록"
    )
    public ResponseEntity<DataResponse> blogUpload(
            @RequestBody ReqBlogDTO reqBlogDTO
    ){

        blogService.save(reqBlogDTO);
        DataResponse dataResponse = new DataResponse();
        dataResponse.setMessage("성공적으로 등록 되었습니다.");
        dataResponse.setStatus(200);
        return ResponseEntity.ok(dataResponse);
    }
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
    @PatchMapping("/blogs/{id}")
    public ResponseEntity<DataResponse> updateBlog(
            @PathVariable Long id,
            @RequestBody ReqBlogDTO reqBlogDTO
    ){
        DataResponse dataResponse = new DataResponse();
        dataResponse.setMessage("정상적으로 수정 되었습니다.");
        blogService.update(id,reqBlogDTO);

        return ResponseEntity.ok(dataResponse);
    }

}

