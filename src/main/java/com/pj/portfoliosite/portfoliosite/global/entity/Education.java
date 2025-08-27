package com.pj.portfoliosite.portfoliosite.global.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Education {
    @Id
    @GeneratedValue
    private Long id;
    private String school; // 학교
    private String schoolStatus; // 졸업/졸업 예정/

    @ManyToOne
    @JoinColumn(name = "portfolio_id")
    private PortFolio portfolio;
}
