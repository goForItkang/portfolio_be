package com.pj.portfoliosite.portfoliosite.global.entity;

import com.pj.portfoliosite.portfoliosite.global.dto.ReqProject;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Project {
    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String role; // 담당 파트
    private String thumbnailURL;
    private String DemonstrationVideo;
    private String people;
    @Column(nullable = false, columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime createdAt;
    private boolean distribution; // 배포 현황
    @ManyToOne(fetch = FetchType.LAZY)   // 프로젝트는 한 명의 User에게 속함
    @JoinColumn(name = "user_id")        // FK 컬럼명
    private User user;
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectBookMark> bookMarks = new ArrayList<>();
    @OneToMany(mappedBy = "project" , cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectComment> comments = new ArrayList<>();
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectSkill> projectSkills = new ArrayList<>();

    public void setUser(User user) {
        this.user = user;
    }
    public void setProject(ReqProject reqProject) {
        this.title = reqProject.getTitle();
        this.description = reqProject.getDescription();
        this.startDate = reqProject.getStartDate();
        this.endDate = reqProject.getEndDate();
        this.distribution = reqProject.isDistribution();
        this.role = reqProject.getRole();
        this.people = reqProject.getPeople();
    }
    //썸네일 이미지 삽입
    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }
    // 시연 영상
    public void setDemonstrationVideo(String demonstrationVideo) {
        this.DemonstrationVideo = demonstrationVideo;
    }
    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
    public void setComments(List<ProjectComment> comments) {
        this.comments = comments;
    }
    public void addBookMark(ProjectBookMark bookMark) {
        this.bookMarks.add(bookMark);
    }
    public void addLike(ProjectLike like) {
        this.likes.add(like);
    }
    public void addSkill(List<ProjectSkill> projectSkills) {
        for (ProjectSkill projectSkill : projectSkills) {
            this.projectSkills.add(projectSkill);
            projectSkill.setProject(this);
        }
    }
    public void updateProject(ReqProject reqProject) {
        if (reqProject.getTitle() != null) {
            this.title = reqProject.getTitle();
        }
        if (reqProject.getDescription() != null) {
            this.description = reqProject.getDescription();
        }
        if (reqProject.getStartDate() != null) {
            this.startDate = reqProject.getStartDate();
        }
        if (reqProject.getEndDate() != null) {
            this.endDate = reqProject.getEndDate();
        }
        this.distribution = reqProject.isDistribution();
        if (reqProject.getRole() != null) {
            this.role = reqProject.getRole();
        }
        if (reqProject.getPeople() != null) {
            this.people = reqProject.getPeople();
        }
    }
    public boolean getDistribution() {
        return distribution;
    }


}
