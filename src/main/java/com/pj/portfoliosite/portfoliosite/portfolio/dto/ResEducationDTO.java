package com.pj.portfoliosite.portfoliosite.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResEducationDTO {
    private Long id;
    private String school;
    private String schoolStatus;
    private Date startDate;
    private Date endDate;
}
