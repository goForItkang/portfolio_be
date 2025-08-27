package com.pj.portfoliosite.portfoliosite.global.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Career {
    @Id
    @GeneratedValue
    private Long id;
    private String companyName;
    private String duty; // 직무
    private String companyPosition; // 직책
    private String date; // 기간  1달 2달 1년
    private String dutyDescription; // 설명

    @ManyToOne
    @JoinColumn(name = "portfolio_id")
    private PortFolio portfolio;

}
