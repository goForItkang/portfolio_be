package com.pj.portfoliosite.portfoliosite.global.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RecruitRoleDto {
    private String role;
    private int count;
    private int people;
    private java.util.List<String> skills;  // 필요 스킬 목록
    
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
