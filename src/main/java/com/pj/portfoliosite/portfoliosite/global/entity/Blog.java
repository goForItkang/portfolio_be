package com.pj.portfoliosite.portfoliosite.global.entity;

import com.pj.portfoliosite.portfoliosite.blog.dto.ReqBlogDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Blog {
    @Id
    @GeneratedValue
    private Long id;

    private String title;
    @Lob
    private String content;

    private int access; // 0이면 비공개 1이면 공개 2 임시저장
    private String category; // 여러개
    private String thumbnailURL; // 썸네일 URL

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BlogBookmark> bookMarks = new ArrayList<>();

    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BlogLike> blogLikes = new ArrayList<>();

    public void addUser(User user) {
        this.user = user;
    }
    public void blogSave(ReqBlogDTO req){
        title = req.getTitle();
        content = req.getContent();
        access = req.getBlogStatus();
        category = req.getCategory();
        if(req.getThumbnail() == null){
            thumbnailURL = null;
        }
        createdAt = LocalDateTime.now();
    }
    public void addBookmark(BlogBookmark bookmark){
        bookMarks.add(bookmark);
    }
    public void addLike(BlogLike like){
        blogLikes.add(like);
    }


}
