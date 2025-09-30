package com.pj.portfoliosite.portfoliosite.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResBlogComment {
    private Long id;
    private String comment; // 댓글 내용
    private Long userId;
    private String userWriteName;
    private String writeProfileImgUrl;
    private LocalDateTime createdAt; // 생성일
    private boolean isOwner; // 작성자 일 경우
    private List<ResBlogComment> replies; // 대 댓글

}
