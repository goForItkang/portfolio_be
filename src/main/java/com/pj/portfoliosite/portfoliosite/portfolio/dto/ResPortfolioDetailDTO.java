package com.pj.portfoliosite.portfoliosite.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 응답 객체로 포트폴리오의 좋아요 북마크 상태, 좋아요 및 북 마크 갯수
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResPortfolioDetailDTO {
    private Long likeCount;
    private Long bookMarkCount;
    private boolean likeCheck;
    private boolean bookMarkCheck;

}
