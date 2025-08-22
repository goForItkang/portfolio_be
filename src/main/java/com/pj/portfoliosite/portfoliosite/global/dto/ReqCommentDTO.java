package com.pj.portfoliosite.portfoliosite.global.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 전체적인 댓글
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReqCommentDTO {
    private String comment;
    private Long parentCommentId;
}

