package com.pj.portfoliosite.portfoliosite.global.entity;

import com.pj.portfoliosite.portfoliosite.portfolio.dto.ReqCareerDTO;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.Date;

@Entity
@Getter
public class Career {
    @Id
    @GeneratedValue
    private Long id;
    private String companyName;
    private String duty; // 직무
    private String companyPosition; // 직책
    private String date; // 기간  1달 2달 1달
    private Date startDate;// 입사 날짜
    private Date endDate; // 퇴사 날짜
    private String dutyDescription; // 설명

    @ManyToOne
    @JoinColumn(name = "portfolio_id")
    private PortFolio portfolio;

    public void ReqCareerDTO(ReqCareerDTO reqCareerDTO) {
        this.companyName = reqCareerDTO.getCompanyName();
        this.duty = reqCareerDTO.getDuty();
        this.companyPosition = reqCareerDTO.getCompanyPosition();
        this.startDate = reqCareerDTO.getStartDate();
        this.endDate = reqCareerDTO.getEndDate();
        if(reqCareerDTO.getDate() == null && reqCareerDTO.getCompanyPosition() == null){
            this.date = null;
        }else{
            this.date = reqCareerDTO.getDate() + " ~ " + reqCareerDTO.getCompanyPosition();
        }
        this.dutyDescription = reqCareerDTO.getDutyDescription();
    }
    public void setPortfolio(PortFolio portfolio) {
        this.portfolio = portfolio;
    }
}
