package com.pj.portfoliosite.portfoliosite.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReqBlogCommentDTO {
    private Long parentCommentId; // entity 참조값
    private String comment; // 임력 값 만 받으면 내부 처리
}
