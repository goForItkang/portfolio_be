package com.pj.portfoliosite.portfoliosite.global.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter

public class Skill {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
}
