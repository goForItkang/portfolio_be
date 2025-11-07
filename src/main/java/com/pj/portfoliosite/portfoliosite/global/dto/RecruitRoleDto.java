package com.pj.portfoliosite.portfoliosite.global.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RecruitRoleDto {
    private Long id;
    private String role;
    private int count;
    private int people;
    
    @JsonIgnore
    private List<Long> skillIds = new ArrayList<>();
    
    private List<SkillDto> skills = new ArrayList<>();
    
    @JsonSetter("skills")
    public void setSkillsFromJson(Object skillsData) {
        if (skillsData == null) {
            this.skills = new ArrayList<>();
            this.skillIds = new ArrayList<>();
            return;
        }
        
        if (skillsData instanceof List) {
            List<?> list = (List<?>) skillsData;
            this.skills = new ArrayList<>();
            this.skillIds = new ArrayList<>();
            
            for (Object item : list) {
                if (item instanceof String) {
                    // 문자열 배열: ["React", "Spring"]
                    this.skills.add(SkillDto.builder()
                            .name((String) item)
                            .build());
                } else if (item instanceof Number) {
                    // ID 배열: [1, 2] - skillIds로 들어온 경우
                    Long skillId = ((Number) item).longValue();
                    this.skillIds.add(skillId);
                } else if (item instanceof java.util.Map) {
                    // 객체 배열: [{"id": 1, "name": "React"}]
                    java.util.Map<?, ?> map = (java.util.Map<?, ?>) item;
                    Object id = map.get("id");
                    Object name = map.get("name");
                    
                    Long skillId = null;
                    if (id instanceof Number) {
                        skillId = ((Number) id).longValue();
                        this.skillIds.add(skillId);
                    }
                    
                    if (name != null) {
                        this.skills.add(SkillDto.builder()
                                .id(skillId)
                                .name(name.toString())
                                .build());
                    }
                }
            }
        }
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SkillDto {
        private Long id;
        private String name;
    }
}
