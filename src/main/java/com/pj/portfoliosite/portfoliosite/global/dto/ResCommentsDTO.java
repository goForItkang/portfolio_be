package com.pj.portfoliosite.portfoliosite.global.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
// 댓글을 종합 적으로 사용할 DTO
public class ResCommentsDTO {
    private Long id;
    private String comment;
    private boolean checkMe; // 본인이 작성한 값인지
    // 사용자 정보
    private String writeName; // nickname 정보
    private String writeId;
    private String profileUrl;
    // 정보
    private Long parentCommentId;
    private LocalDateTime createdAt;
}
