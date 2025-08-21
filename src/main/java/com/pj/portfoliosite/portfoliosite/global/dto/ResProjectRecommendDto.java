package com.pj.portfoliosite.portfoliosite.global.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 추천 프로젝트
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResProjectRecommendDto {
    private Long id;
    private String title;
    private String writeName; // 작성자
    private String thumbnailURL; //썸네일 URL
}
