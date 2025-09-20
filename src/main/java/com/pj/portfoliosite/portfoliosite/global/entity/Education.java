package com.pj.portfoliosite.portfoliosite.global.entity;

import com.pj.portfoliosite.portfoliosite.portfolio.dto.ReqEducationDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Education {
    @Id
    @GeneratedValue
    private Long id;
    private String school; // 학교
    private Date startDate;
    private Date endDate; // 이거 null 가능
    private String schoolStatus; // 졸업/졸업 예정/

    @ManyToOne
    @JoinColumn(name = "portfolio_id")
    private PortFolio portfolio;

    public void ReqEducationDTO(ReqEducationDTO reqEducationDTO) {
        this.school = reqEducationDTO.getSchool();
        this.schoolStatus = reqEducationDTO.getSchoolStatus();
        this.startDate = reqEducationDTO.getStartDate();
        this.endDate = reqEducationDTO.getEndDate();
        
    }
    public void setPortfolio(PortFolio portfolio) {
        this.portfolio = portfolio;
    }
}
