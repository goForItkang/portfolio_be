package com.pj.portfoliosite.portfoliosite.global.entity;

import com.pj.portfoliosite.portfoliosite.portfolio.dto.ReqCareerDTO;
import jakarta.persistence.*;
import lombok.Getter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        this.startDate = parseDate(reqCareerDTO.getStartDate());
        this.endDate = parseDate(reqCareerDTO.getEndDate());
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
    private Date parseDate(String dateString) {
        // 입력된 문자열이 유효한지 확인
        if (dateString == null || dateString.trim().isEmpty()) {
            return null; // 비어있으면 null 반환
        }
        try {
            // "yyyy-MM-dd" 형식으로 파싱 시도
            return new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
        } catch (ParseException e) {
            // 날짜 형식에 맞지 않으면 경고 로그를 남기고 null 반환
            // System.err.println("잘못된 날짜 형식입니다: " + dateString);
            return null;
        }
    }

}
