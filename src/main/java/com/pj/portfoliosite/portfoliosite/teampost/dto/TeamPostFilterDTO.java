package com.pj.portfoliosite.portfoliosite.teampost.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamPostFilterDTO {
    private List<String> skills;
    private String recruitStatus;
    private String sortBy; // 최신순, 인기순, 마감순
}