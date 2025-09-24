package com.pj.portfoliosite.portfoliosite.teampost.dto;

import com.pj.portfoliosite.portfoliosite.global.dto.RecruitRoleDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReqTeamPostDTO {
    private String title;                        // 제목
    private String content;                      // 내용
    private String projectType;                  // 프로젝트 유형 (웹, 앱, 게임 등)
    
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @JsonDeserialize(using = FlexibleLocalDateDeserializer.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate recruitDeadline;           // 모집 마감일 (날짜만)
    
    private String contactMethod;                // 연락 방법
    private boolean saveStatus;                  // 임시저장 여부
    private List<String> skills;                 // 필요 기술스택 (배열)
    private List<RecruitRoleDto> recruitRoles;   // 모집 역할 목록
}