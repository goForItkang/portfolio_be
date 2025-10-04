package com.pj.portfoliosite.portfoliosite.teampost.dto;

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
    private String projectType;
    private LocalDateTime createdAt;
    private String recruitStatus;
    private int viewCount;
    private int likeCount;
    private List<String> requiredRoles;
    private LocalDate recruitDeadline;
}