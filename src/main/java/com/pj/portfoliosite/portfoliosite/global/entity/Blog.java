package com.pj.portfoliosite.portfoliosite.global.entity;

import com.pj.portfoliosite.portfoliosite.blog.dto.ReqBlogDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

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

}
