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
public class ReqTeamPostDTO {
    private String title;                        // 제목
    private String content;                      // 내용
    private String projectType;                  // 프로젝트 유형 (웹, 앱, 게임 등)
    private LocalDateTime recruitDeadline;       // 모집 마감일
    private String contactMethod;                // 연락 방법
    private boolean saveStatus;                  // 임시저장 여부
    private String skills;                       // 필요 기술스택
    private List<RecruitRoleDto> recruitRoles;   // 모집 역할 목록
}