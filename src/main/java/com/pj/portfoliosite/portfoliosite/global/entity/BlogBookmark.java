package com.pj.portfoliosite.portfoliosite.global.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@Entity
@Table(
        name = "blog_book_mark",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "blog_id"})
        }
)
public class BlogBookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id", nullable = false)
    private Blog blog;

    public void addUser(User user) {
        this.user = user;
    }
    public void addBlog(Blog blog) {
        this.blog = blog;
    }
}
