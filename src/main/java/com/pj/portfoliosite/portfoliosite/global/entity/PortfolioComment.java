package com.pj.portfoliosite.portfoliosite.global.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class PortfolioComment {
    @Id
    @GeneratedValue
    private Long id;
    private String comment;
    private LocalDateTime createdAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private PortfolioComment parent;   // 참조 댓글

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PortfolioComment> replies = new ArrayList<>();  // 대댓글 목록

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // userID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="portfolio_id")
    private PortFolio portfolio;

    public void addUser(User user) {
        this.user = user;
    }
    public void addReply(PortfolioComment reply) {
        replies.add(reply);
    }
    public void addProject(PortFolio portfolio) {
        this.portfolio = portfolio;
    }

    public PortfolioComment (String Comment,User user,PortFolio portfolio){
        this.comment = Comment;
        this.user = user;
        this.portfolio = portfolio;
    }
    @PrePersist
    public void prePersist(){
        if(createdAt == null){
            createdAt = LocalDateTime.now();
        }
    }
    public void setParent(PortfolioComment parent) {
        this.parent = parent;
    }
    public void updateComment(String comment) {
        this.comment = comment;
    }

}
