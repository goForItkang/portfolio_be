package com.pj.portfoliosite.portfoliosite.mypage.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResCommentActivityDTO {
    private Long commentId;
    private String comment;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    // 게시글 정보
    private Long postId;
    private String postTitle;
    private String postType; // "project", "portfolio", "teampost"
}
