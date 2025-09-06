package com.pj.portfoliosite.portfoliosite.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResCareerDTO {
    private Long id;
    private String companyName;
    private String duty; // 직무
    private String companyPosition; // 직책
    private String date; // 기간  1달 2달 1년
    private String dutyDescription; // 설명
}
