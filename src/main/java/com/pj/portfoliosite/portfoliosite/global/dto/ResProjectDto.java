package com.pj.portfoliosite.portfoliosite.global.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResProjectDto {
    private Long id; //pk 값
    private String title; // 제목
    private String description; // 설명
    private String writeName; // 작성자
    private String thumbnailURL; //썸네일 URL
}
