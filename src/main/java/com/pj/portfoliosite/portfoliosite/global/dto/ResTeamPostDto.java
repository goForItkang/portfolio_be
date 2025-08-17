package com.pj.portfoliosite.portfoliosite.global.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResTeamPostDto {
    private Long id;
    private String title;
    private String writer; // 작성자
    private LocalDateTime createdAt; // 작성 시간
    private List<RecruitRoleDto> recruitRoles; // 직군별 파트

}
