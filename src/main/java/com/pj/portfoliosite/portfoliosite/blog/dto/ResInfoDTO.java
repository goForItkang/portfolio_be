package com.pj.portfoliosite.portfoliosite.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResInfoDTO {
    private Long likeCount;
    private Long BookMarkCount;
    private boolean bookMarkCheck;
    private boolean likeCheck;
}
