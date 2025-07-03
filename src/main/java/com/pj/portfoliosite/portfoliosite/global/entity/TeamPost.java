package com.pj.portfoliosite.portfoliosite.global.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class TeamPost {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String title;
    @Lob
    private String content;
    // 모집하는 파트
    @OneToMany(mappedBy = "teamPost", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RecruitRole> recruitRoles = new ArrayList<>();

    // 수정필요
    private int status; //0 이면 모집중 1 이면 모집완료
    @CreationTimestamp
    private LocalDateTime created_at;

    @OneToMany(mappedBy = "teamPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamPostChat> teamPostChatList = new ArrayList<>();

    public void addRecruitRole(List<RecruitRole> recruitRoles) {
        this.recruitRoles.addAll(recruitRoles);
    }
}
