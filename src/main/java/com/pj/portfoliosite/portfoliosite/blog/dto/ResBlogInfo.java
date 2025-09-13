package com.pj.portfoliosite.portfoliosite.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
// 좋아요랑 북 마크 관련
public class ResBlogInfo {
    private Long likeCount;
    private Long BookMarkCount;
    private boolean bookMarkCheck;
    private boolean likeCheck;
}
