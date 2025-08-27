package com.pj.portfoliosite.portfoliosite.global.entity;

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
public class PortFolio {
    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String email;
    private String industry; // 분야
    private String jobPosition;; //직무
    private String skill; // 스킬
    private String introductions; // 본인 소개

    private LocalDateTime createAt; // 작성일
    // 임시 저장
    private boolean saveStatus; // 저장 상태 임시저장/사용자에게 보여줄건지
    //문서 파트 수정해야함
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "portfolio")
    private List<Career> careers = new ArrayList<>();

    @OneToMany(mappedBy = "portfolio")
    private List<Award> awards = new ArrayList<>();

    @OneToMany(mappedBy = "portfolio")
    private List<Certificate> certificates = new ArrayList<>();

    @OneToMany(mappedBy = "portfolio")
    private List<Education> educations = new ArrayList<>();

}
