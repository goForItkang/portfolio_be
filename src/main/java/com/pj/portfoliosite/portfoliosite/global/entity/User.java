package com.pj.portfoliosite.portfoliosite.global.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@EntityListeners(UserEncryptionListener.class) // 암호화 리스너 재활성화
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String email; // AES 암호화됨
    private String password; // BCrypt 해시됨 (복호화 불가)
    private String name; // AES 암호화됨
    private LocalDateTime birthday; // 암호화 안함 (검색/통계용)
    private String job; // AES 암호화됨
    private String interest; // AES 암호화됨
    private String interest2; // AES 암호화됨
    private String tech_stack; // 암호화 안함 (공개 정보)
    private String gitLink; // AES 암호화됨
    private String nickname; // AES 암호화됨
    @Lob
    private String introduce; // AES 암호화됨
    private String profile; // AES 암호화됨 (이미지 URL)
    private String createAt; // 암호화 안함

    private String provider = "LOCAL"; // 암호화 안함
    private String providerId; // 암호화 안함

    private String refreshToken; // 암호화 안함 (시스템용)
    private LocalDateTime refreshTokenExpiry; // 암호화 안함 (시스템용)

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamPost> teamPosts = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Blog> blogs = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<PortFolio> portPolios = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Project> projects = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<ProjectComment> projectComments = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<PortfolioComment> portfolioComments = new ArrayList<>();


    public void addBlog(Blog blog) {
        blogs.add(blog);
    }
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamPostComment> teamPostComments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamPostLike> teamPostLikes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamPostBookMark> teamPostBookMarks = new ArrayList<>();

    public void addProject(Project project) {
        projects.add(project);
    }

    // 편의 메서드
    public void addTeamPost(TeamPost teamPost) {
        teamPosts.add(teamPost);
    }
}

