package com.pj.portfoliosite.portfoliosite.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReqPortfolioDTO {
    private String title;
    private String email;
    private String industry;       // 분야
    private String jobPosition;    // 직업
    private String introductions;  // 본인 소개
    private boolean saveStatus;    // 임시 저장 여부
    // 연관관계 엔티티들 DTO 리스트
    private MultipartFile file;
    private List<ReqProjectDescription> projectDescriptions;
    private List<ReqCareerDTO> careers;
    private List<ReqAwardDTO> awards;
    private List<ReqCertificateDTO> certificates;
    private List<ReqEducationDTO> educations;
}
