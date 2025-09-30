package com.pj.portfoliosite.portfoliosite.global.entity;

import com.pj.portfoliosite.portfoliosite.blog.dto.ReqBlogCommentDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class BlogComment {
    @Id
    @GeneratedValue
    private Long id;
    private String comment;
    private LocalDateTime createdAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private BlogComment parent;   // 참조 댓글

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BlogComment> replies = new ArrayList<>();  // 대댓글 목록

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // userID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="blog_id")
    private Blog blog;

    public void addUser(User user) {
        this.user = user;
    }
    public void addReply(BlogComment reply) {
        replies.add(reply);
    }
    public void addBlog(Blog blog) {
        this.blog = blog;
    }

    public BlogComment (String Comment,User user,Blog blog){
        this.comment = Comment;
        this.user = user;
        this.blog = blog;
        this.createdAt = LocalDateTime.now();
    }
    public void commentSave(String comment){
        this.comment = comment;
    }
    @PrePersist
    public void prePersist(){
        if(createdAt == null){
            createdAt = LocalDateTime.now();
        }
    }
    public void setParent(BlogComment parent) {
        this.parent = parent;
    }
    public void updateComment(String comment) {
        this.comment = comment;
    }
}
