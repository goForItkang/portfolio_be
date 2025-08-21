package com.pj.portfoliosite.portfoliosite.global.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@Getter
@AllArgsConstructor
public class RecruitRole {
    @Id
    @GeneratedValue
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
}
