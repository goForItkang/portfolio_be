package com.pj.portfoliosite.portfoliosite.global.entity;

import com.pj.portfoliosite.portfoliosite.portfolio.dto.ReqAwardDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
// 수상 경력
public class Award {
    @Id
    @GeneratedValue
    private Long id;
    private String awardDescription; // 설명 이겠죵~

    @ManyToOne
    @JoinColumn(name = "portfolio_id")
    private PortFolio portfolio;

    public void addReqAwardDTO(ReqAwardDTO reqAwardDTO) {
        this.awardDescription = reqAwardDTO.getAwardDescription();
    }
}
