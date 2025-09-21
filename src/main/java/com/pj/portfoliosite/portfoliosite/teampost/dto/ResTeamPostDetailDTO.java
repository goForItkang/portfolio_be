package com.pj.portfoliosite.portfoliosite.teampost.dto;

import com.pj.portfoliosite.portfoliosite.global.dto.RecruitRoleDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResTeamPostDetailDTO {
    private Long id;
    private String title;
    private String content;
    private String writerName;
    private String projectType;
    private LocalDateTime createdAt;
    private LocalDateTime recruitDeadline;
    private String contactMethod;
    private String skills;
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