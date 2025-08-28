package com.pj.portfoliosite.portfoliosite.global.entity;

import com.pj.portfoliosite.portfoliosite.portfolio.dto.ReqCertificateDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Date;

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
}
