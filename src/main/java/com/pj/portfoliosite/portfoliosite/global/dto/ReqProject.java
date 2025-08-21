package com.pj.portfoliosite.portfoliosite.global.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReqProject {
    private String title;
    private String description;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;   // 날짜만 필요
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;     // 날짜만 필요
    private String role;
    private String skill;
    private boolean distribution;
    private String projectURL; // 프로젝트 URL
    private MultipartFile thumbnailImg; // null 일 수 있음
    private MultipartFile demonstrationVideo; //null 일 수 있음

}
