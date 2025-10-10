package com.pj.portfoliosite.portfoliosite.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResProjectDetailDto {
    private Long likeCount;
    private Long bookMarkCount;
    private boolean likeCheck;
    private boolean bookMarkCheck;
}
