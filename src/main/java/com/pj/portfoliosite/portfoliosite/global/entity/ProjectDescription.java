package com.pj.portfoliosite.portfoliosite.global.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class ProjectDescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;

    @ManyToOne
    @JoinColumn(name = "portfolio_id", nullable = false)
    private PortFolio portfolio;

    public void addPortfolio(PortFolio portfolio) {
        this.portfolio = portfolio;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
