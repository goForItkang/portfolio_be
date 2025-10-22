package com.pj.portfoliosite.portfoliosite.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResWorkLikeDTO {
    private Long id;
    private String title;
    private LocalDateTime createTime;
    private String type;
    private String description;

}
