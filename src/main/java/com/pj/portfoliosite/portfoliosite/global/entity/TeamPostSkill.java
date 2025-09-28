package com.pj.portfoliosite.portfoliosite.global.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamPostSkill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_post_id")
    private TeamPost teamPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id")
    private Skill skill;

    public TeamPostSkill(TeamPost teamPost, Skill skill) {
        this.teamPost = teamPost;
        this.skill = skill;
    }
    
    public void setTeamPost(TeamPost teamPost) {
        this.teamPost = teamPost;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }
}