package com.pj.portfoliosite.portfoliosite.global.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 추천 프로젝트
@Data
@NoArgsConstructor
@AllArgsConstructor
/*메인화면에 출력 되는 추천 프로젝트*/
public class ResProjectRecommendDto {
    private Long id; //pk 값
    private String title; // 제목
    private String description; // 설명
    private String writeName; // 작성자
    private String thumbnailURL; //썸네일 URL
}
