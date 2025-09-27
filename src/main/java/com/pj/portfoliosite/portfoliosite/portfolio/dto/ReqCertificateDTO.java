package com.pj.portfoliosite.portfoliosite.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReqCertificateDTO {
    private String certificateName;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date certificateDate; // 취득일
    private String number; // 등록 번호
}
