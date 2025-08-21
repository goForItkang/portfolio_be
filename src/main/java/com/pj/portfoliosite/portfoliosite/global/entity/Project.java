package com.pj.portfoliosite.portfoliosite.global.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private Date startDate;
    private Date endDate;
    private String role; // 담당 파트
    private String thumbnailURL;
    private String DemonstrationVideo;
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)   // 프로젝트는 한 명의 User에게 속함
    @JoinColumn(name = "user_id")        // FK 컬럼명
    private User user;

    public void setUser(User user) {
        this.user = user;
    }
}
