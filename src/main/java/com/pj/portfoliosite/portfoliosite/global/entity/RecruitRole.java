package com.pj.portfoliosite.portfoliosite.global.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class RecruitRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String role;
    private int count;
    private int people;
    
    @Column(columnDefinition = "TEXT")
    private String skillsJson;  // 스킬 목록을 JSON 문자열로 저장

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_post_id") // FK
    private TeamPost teamPost;

    @Builder
    public RecruitRole(String role, int count, TeamPost teamPost) {
        this.role = role;
        this.count = count;
        this.people = 0; // default
        this.teamPost = teamPost;
    }
    
    // 편의 메서드
    public void setTeamPost(TeamPost teamPost) {
        this.teamPost = teamPost;
    }
    
    @Transient
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    public List<SkillInfo> getSkills() {
        if (skillsJson == null || skillsJson.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(skillsJson, 
                objectMapper.getTypeFactory().constructCollectionType(List.class, SkillInfo.class));
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    public void setSkills(List<?> skills) {
        if (skills == null || skills.isEmpty()) {
            this.skillsJson = null;
            return;
        }
        
        try {
            List<SkillInfo> skillInfos = new ArrayList<>();
            for (Object skill : skills) {
                if (skill instanceof SkillInfo) {
                    skillInfos.add((SkillInfo) skill);
                } else if (skill instanceof java.util.Map) {
                    // 프론트엔드에서 보낸 객체 형태 처리
                    java.util.Map<?, ?> map = (java.util.Map<?, ?>) skill;
                    Long skillId = null;
                    String name = null;
                    
                    Object id = map.get("id");
                    if (id instanceof Number) {
                        skillId = ((Number) id).longValue();
                    }
                    
                    Object nameObj = map.get("name");
                    if (nameObj != null) {
                        name = nameObj.toString();
                    }
                    
                    if (skillId != null && name != null) {
                        skillInfos.add(new SkillInfo(skillId, name));
                    }
                } else if (skill instanceof String) {
                    // 문자열로만 온 경우 (호환성)
                    skillInfos.add(new SkillInfo(null, (String) skill));
                }
            }
            this.skillsJson = objectMapper.writeValueAsString(skillInfos);
        } catch (Exception e) {
            this.skillsJson = null;
        }
    }
    
    public List<String> getSkillsAsStrings() {
        if (skillsJson == null || skillsJson.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(skillsJson, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            // 새 형식 (객체 배열)인 경우
            try {
                List<SkillInfo> skillInfos = getSkills();
                return skillInfos.stream()
                        .map(SkillInfo::getName)
                        .toList();
            } catch (Exception ex) {
                return new ArrayList<>();
            }
        }
    }
    
    public void setSkillsAsStrings(List<String> skills) {
        if (skills == null || skills.isEmpty()) {
            this.skillsJson = null;
        } else {
            try {
                this.skillsJson = objectMapper.writeValueAsString(skills);
            } catch (Exception e) {
                this.skillsJson = null;
            }
        }
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkillInfo {
        private Long id;
        private String name;
    }
}
