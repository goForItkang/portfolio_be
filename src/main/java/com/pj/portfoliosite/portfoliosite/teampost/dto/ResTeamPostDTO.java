package com.pj.portfoliosite.portfoliosite.teampost.dto;

import com.pj.portfoliosite.portfoliosite.global.dto.RecruitRoleDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResTeamPostDTO {
    private Long id;
    private String title;
    private String writerName;
    private LocalDateTime createdAt;
    private String recruitStatus;
    private int viewCount;
    private int likeCount;
    private List<RecruitRoleDto> requiredRoles;  // String 리스트에서 RecruitRoleDto 리스트로 변경
    private LocalDate recruitDeadline;
}
