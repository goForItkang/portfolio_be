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
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    private String name;
    private LocalDateTime birthday; // 생년월일
    private String job;
    private String interest; //만약 관심이 PM이면,
    private String interest2;
    private String tech_stack;
    private String gitLink;
    @Lob
    private String introduce; // 자기소개
    private String profile; // 이미지 url
    private String createAt;

    private String refreshToken;
    private LocalDateTime refreshTokenExpiry;

    @OneToMany(mappedBy = "user")
    private List<TeamPost> teamPosts = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Blog> blogs = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<PortFolio> portPolios = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Project> projects = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<ProjectComment> projectComments = new ArrayList<>();


    public void addProject(Project project) {
        projects.add(project);
    }
}
