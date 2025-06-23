package com.pj.portfoliosite.portfoliosite.global.entity;

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
    private int access; // 0이면 비공개 1이면 공개
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
