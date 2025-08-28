package com.pj.portfoliosite.portfoliosite.global.entity;

import com.pj.portfoliosite.portfoliosite.portfolio.dto.ReqCareerDTO;
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

    public void ReqCareerDTO(ReqCareerDTO reqCareerDTO) {
        this.companyName = reqCareerDTO.getCompanyName();
        this.duty = reqCareerDTO.getDuty();
        this.companyPosition = reqCareerDTO.getCompanyPosition();
        this.date = reqCareerDTO.getDate();
        this.dutyDescription = reqCareerDTO.getDutyDescription();
        this.portfolio = new PortFolio();
    }
}
