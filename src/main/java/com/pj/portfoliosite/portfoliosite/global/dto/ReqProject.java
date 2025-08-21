package com.pj.portfoliosite.portfoliosite.global.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReqProject {
    private String title;
    private String description;
    private Date startDate;
    private Date endDate;
    private String role;
    private String skill;
    private String projectURL; // 프로젝트 URL
    private MultipartFile thumbnailImg; // null 일 수 있음
    private MultipartFile demonstrationVideo; //null 일 수 있음

}
