package com.pj.portfoliosite.portfoliosite.global.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String name;


    @OneToMany(mappedBy = "skill")
    private List<PortfolioSkill> portfolioSkills = new ArrayList<>();

    
    // TeamPostSkill과의 연관관계
    @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamPostSkill> teamPostSkills = new ArrayList<>();

    @OneToMany(mappedBy = "skill",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectSkill> projectSkills = new ArrayList<>();

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
