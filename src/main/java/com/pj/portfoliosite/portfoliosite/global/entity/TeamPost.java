package com.pj.portfoliosite.portfoliosite.global.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
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
    private String image;
    // 모집하는 파트
    @ElementCollection
    @CollectionTable(name = "recruit_part_count", joinColumns = @JoinColumn(name = "recruit_id"))
    @MapKeyColumn(name = "part")
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "count")
    private Map<TeamPostPart, Integer> partCounts = new HashMap<>();

    // 수정필요
    private int status; //0 이면 모집중 1 이면 모집완료
    private LocalDateTime created_at;

    @OneToMany(mappedBy = "teamPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamPostChat> teamPostChatList = new ArrayList<>();

}
