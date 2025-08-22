package com.pj.portfoliosite.portfoliosite.global.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Persistent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class ProjectComment {
    @Id
    @GeneratedValue
    private Long id;
    private String comment;
    private LocalDateTime createdAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private ProjectComment parent;   // 참조 댓글

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectComment> replies = new ArrayList<>();  // 대댓글 목록

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // userID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="project_id")
    private Project project;

    public void addUser(User user) {
        this.user = user;
    }
    public void addReply(ProjectComment reply) {
        replies.add(reply);
    }
    public void addProject(Project project) {
        this.project = project;
    }

    public ProjectComment (String Comment,User user,Project project){
        this.comment = Comment;
        this.user = user;
        this.project = project;
    }
    @PrePersist
    public void prePersist(){
        if(createdAt == null){
            createdAt = LocalDateTime.now();
        }
    }
    public void setParent(ProjectComment parent) {
        this.parent = parent;
    }
}
