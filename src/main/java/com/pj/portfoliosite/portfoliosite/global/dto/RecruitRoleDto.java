package com.pj.portfoliosite.portfoliosite.global.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RecruitRoleDto {
    private String role;
    private int count;
    private int people;         
    private java.util.List<String> skills;  // 필요 스킬 목록
}
