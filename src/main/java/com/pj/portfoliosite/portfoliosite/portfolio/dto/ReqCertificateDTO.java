package com.pj.portfoliosite.portfoliosite.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReqCertificateDTO {
    private String certificateName;
    private String certificateDate; // 취득일
    private String number; // 등록 번호
}
