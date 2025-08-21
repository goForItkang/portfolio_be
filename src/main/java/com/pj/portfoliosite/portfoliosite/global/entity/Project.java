package com.pj.portfoliosite.portfoliosite.global.entity;

import com.pj.portfoliosite.portfoliosite.global.dto.ReqProject;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

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
    @Column(nullable = false, columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)   // 프로젝트는 한 명의 User에게 속함
    @JoinColumn(name = "user_id")        // FK 컬럼명
    private User user;

    public void setUser(User user) {
        this.user = user;
    }
    public void setProject(ReqProject reqProject) {
        this.title = reqProject.getTitle();
        this.description = reqProject.getDescription();
        this.startDate = reqProject.getStartDate();
        this.endDate = reqProject.getEndDate();
        this.role = reqProject.getRole();
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
}
