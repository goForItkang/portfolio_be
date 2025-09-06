package com.pj.portfoliosite.portfoliosite.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResUser {
    private Long id;
    private String email;
    private String name;
    private String profileImgUrl;

}
