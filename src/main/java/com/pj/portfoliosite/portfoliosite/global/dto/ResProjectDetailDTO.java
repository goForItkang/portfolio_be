package com.pj.portfoliosite.portfoliosite.global.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
//프로젝트 상세페이지에 출력DTO
public class ResProjectDetailDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDate startDate; //프로젝트 시작 기간
    private LocalDate endDate; // 프로젝트 끝난 기간
    private String role; // 담당 역활
//  private List<Skil> // skill
    private String skill; //skill 처리를 어떻게 애할지안정함
    private String demonstrationVideoUrl;

    // 댓글
    // 댓글 리스트에 사용profile 및 댓글 정보 본인 여부를 확인함
    List<ResCommentsDTO> resCommentsDTOList;

}
