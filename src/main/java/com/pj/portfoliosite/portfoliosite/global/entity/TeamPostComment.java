package com.pj.portfoliosite.portfoliosite.global.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class TeamPostComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String comment;
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private TeamPostComment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamPostComment> replies = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_post_id")
    private TeamPost teamPost;

    public TeamPostComment(String comment, User user, TeamPost teamPost) {
        this.comment = comment;
        this.user = user;
        this.teamPost = teamPost;
    }

    public void setParent(TeamPostComment parent) {
        this.parent = parent;
    }

    public void addReply(TeamPostComment reply) {
        if (reply != null) {
            replies.add(reply);
        }
    }

    public void setTeamPost(TeamPost teamPost) {
        this.teamPost = teamPost;
    }

    public void updateComment(String comment) {
        this.comment = comment;
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}