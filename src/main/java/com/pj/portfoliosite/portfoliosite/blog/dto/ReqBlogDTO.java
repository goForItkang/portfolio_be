package com.pj.portfoliosite.portfoliosite.blog.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReqBlogDTO {
    private String title;
    private String content;
    private MultipartFile thumbnail; // null 일 수 있음
    private String category; // 카테고리 변환 작업(자기 개발 or 공우)
    private int blogStatus; //공개 1, 비공개 2, ,임시 작성  3

}
