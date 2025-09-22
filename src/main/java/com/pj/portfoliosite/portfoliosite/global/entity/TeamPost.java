package com.pj.portfoliosite.portfoliosite.global.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;                    // 제목

    @Lob
    private String content;                  // 내용

    private String projectType;              // 프로젝트 유형 (웹, 앱, 게임 등)
    private String skills;                   // 필요 기술스택
    private String contactMethod;            // 연락방법
    private LocalDateTime recruitDeadline;   // 모집마감일

    @Enumerated(EnumType.STRING)
    private RecruitStatus recruitStatus = RecruitStatus.RECRUITING; // 모집상태

    private boolean saveStatus = false;      // 임시저장 여부
    private int viewCount = 0;              // 조회수

    @Column(nullable = false, columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime createdAt;

    // 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "teamPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamPostComment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "teamPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamPostLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "teamPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamPostBookMark> bookMarks = new ArrayList<>();

    @OneToMany(mappedBy = "teamPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecruitRole> recruitRoles = new ArrayList<>();

    // 편의 메서드
    public void setUser(User user) {
        this.user = user;
    }

    public void addComment(TeamPostComment comment) {
        this.comments.add(comment);
        comment.setTeamPost(this);
    }

    public void addLike(TeamPostLike like) {
        this.likes.add(like);
        like.setTeamPost(this);
    }

    public void addBookMark(TeamPostBookMark bookMark) {
        this.bookMarks.add(bookMark);
        bookMark.setTeamPost(this);
    }

    public void addRecruitRole(RecruitRole recruitRole) {
        this.recruitRoles.add(recruitRole);
        recruitRole.setTeamPost(this);
    }

    // 조회수 증가
    public void increaseViewCount() {
        this.viewCount++;
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}