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
    
    // 스킬 목록을 List<String>으로 변환
    @Transient
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    public List<String> getSkills() {
        if (skillsJson == null || skillsJson.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(skillsJson, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    public void setSkills(List<String> skills) {
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
}
