package com.pj.portfoliosite.portfoliosite.global.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String name;
    
    // TeamPostSkill과의 연관관계
    @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamPostSkill> teamPostSkills = new ArrayList<>();
    
    // 편의 생성자
    public Skill(String name) {
        this.name = name;
    }
    
    // 편의 메서드
    public void addTeamPostSkill(TeamPostSkill teamPostSkill) {
        this.teamPostSkills.add(teamPostSkill);
        teamPostSkill.setSkill(this);
    }
}
