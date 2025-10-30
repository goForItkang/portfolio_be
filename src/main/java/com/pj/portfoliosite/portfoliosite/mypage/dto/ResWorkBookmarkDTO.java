package com.pj.portfoliosite.portfoliosite.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 북마크 응답 DTO - 좋아요 파트와 동일한 구조
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResWorkBookmarkDTO {
    private Long id;
    private String title;
    private LocalDateTime createTime;
    private String description;
    private String thumbnailURL;
    private String type;  // "blog", "project", "portfolio", "teampost"
}
