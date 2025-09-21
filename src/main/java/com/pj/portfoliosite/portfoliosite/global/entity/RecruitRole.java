package com.pj.portfoliosite.portfoliosite.global.entity;

import jakarta.persistence.*;
import lombok.*;

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
}
