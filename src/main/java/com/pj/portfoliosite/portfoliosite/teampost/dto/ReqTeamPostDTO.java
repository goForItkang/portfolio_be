package com.pj.portfoliosite.portfoliosite.teampost.dto;

import com.pj.portfoliosite.portfoliosite.global.dto.RecruitRoleDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReqTeamPostDTO {
    private String title;                        // 제목
    private String content;                      // 내용
    
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @JsonDeserialize(using = FlexibleLocalDateDeserializer.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate recruitDeadline;           // 모집 마감일 (날짜만)
    
    private String contactMethod;                // 연락 방법
    private boolean saveStatus;                  // 임시저장 여부
    private List<String> skills;                 // 필요 기술스택 (배열)
    private List<RecruitRoleDto> recruitRoles;   // 모집 역할 목록
    
    // 유연한 역직렬화: 문자열 배열 또는 객체 배열 모두 지원
    @JsonSetter("skills")
    public void setSkills(Object skillsData) {
        if (skillsData == null) {
            this.skills = new ArrayList<>();
            return;
        }
        
        if (skillsData instanceof List) {
            List<?> list = (List<?>) skillsData;
            this.skills = new ArrayList<>();
            
            for (Object item : list) {
                if (item instanceof String) {
                    // 문자열 배열: ["React", "Spring"]
                    this.skills.add((String) item);
                } else if (item instanceof java.util.Map) {
                    // 객체 배열: [{"id": 1, "name": "React"}]
                    java.util.Map<?, ?> map = (java.util.Map<?, ?>) item;
                    Object name = map.get("name");
                    if (name != null) {
                        this.skills.add(name.toString());
                    }
                }
            }
        }
    }
}