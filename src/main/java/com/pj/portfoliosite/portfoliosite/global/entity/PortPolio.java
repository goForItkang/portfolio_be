package com.pj.portfoliosite.portfoliosite.global.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PortPolio {
    @Id
    @GeneratedValue
    private Long id;
    private String title;
    // 설명 및 개요
    private String description;

    private String imgUrl; //대표 이미지 url
    private String videoUrl; // 비디오 Url
    private String gitLink;
    private String part; // 담당 파트
    private String functionDescription; //담당 파트 설명
    private int memberCount;// 참여인원
    @Lob
    private String retrospective; // 회고록
    private String createAt;
    //문서 파트 수정해야함
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


}
