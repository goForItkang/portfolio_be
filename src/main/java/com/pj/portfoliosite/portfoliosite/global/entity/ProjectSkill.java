package com.pj.portfoliosite.portfoliosite.global.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class ProjectSkill {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST) // 새로운 Skill도 저장되도록 PERSIST 옵션 추가
    @JoinColumn(name = "skill_id")
    private Skill skill;
}
