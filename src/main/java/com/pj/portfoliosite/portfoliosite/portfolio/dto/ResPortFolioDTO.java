package com.pj.portfoliosite.portfoliosite.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
// 응답 객체
public class ResPortFolioDTO {
    private Long id;
    private String title;
    private String email;
    private String industry;       // 분야
    private String jobPosition;    // 직무
    private String skill;          // 스킬
    private String introductions;  // 본인 소개
    private LocalDateTime createAt;
    private boolean saveStatus;    // 임시 저장 여부

    // 연관관계 엔티티들 DTO 리스트
    private List<ResProjectDescription> projectDescriptions;
    private List<ResCareerDTO> careers;
    private List<ResAwardDTO> awards;
    private List<ResCertificateDTO> certificates;
    private List<ResEducationDTO> educations;
}
