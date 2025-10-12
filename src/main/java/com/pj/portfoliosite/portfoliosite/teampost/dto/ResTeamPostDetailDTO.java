package com.pj.portfoliosite.portfoliosite.teampost.dto;

import com.pj.portfoliosite.portfoliosite.global.dto.RecruitRoleDto;
import com.pj.portfoliosite.portfoliosite.skill.ResSkill;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.pj.portfoliosite.portfoliosite.teampost.dto.FlexibleLocalDateDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResTeamPostDetailDTO {
    private Long id;
    private String title;
    private String content;
    private String writerName;
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @JsonDeserialize(using = FlexibleLocalDateDeserializer.class)
    private LocalDate recruitDeadline;
    
    private String contactMethod;
    private List<ResSkill> skills;
    private String recruitStatus;
    private int viewCount;

    // 상호작용 정보
    private boolean isLiked;
    private boolean isBookmarked;
    private boolean isOwner;
    private Long likeCount;
    private Long bookmarkCount;

    // 연관 정보
    private List<ResTeamCommentListDTO> comments;
    private List<RecruitRoleDto> recruitRoles;
}