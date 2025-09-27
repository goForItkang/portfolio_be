package com.pj.portfoliosite.portfoliosite.global.entity;

import com.pj.portfoliosite.portfoliosite.portfolio.dto.ReqCertificateDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Certificate {
    @Id
    @GeneratedValue
    private Long id;
    private String certificateName;
    private Date certificateDate; // 취득일
    private String number; // 등록 번호

    @ManyToOne
    @JoinColumn(name = "portfolio_id")
    private PortFolio portfolio;

    public void reqCertificateDTO(ReqCertificateDTO reqCertificateDTO) {
        this.certificateName = reqCertificateDTO.getCertificateName();
        this.certificateDate = reqCertificateDTO.getCertificateDate();
        this.number = reqCertificateDTO.getNumber();
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
