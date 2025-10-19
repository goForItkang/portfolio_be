package com.pj.portfoliosite.portfoliosite.mypage.dto;

import com.pj.portfoliosite.portfoliosite.blog.dto.ResBlogDTO;
import com.pj.portfoliosite.portfoliosite.global.dto.ResProjectDto;
import com.pj.portfoliosite.portfoliosite.portfolio.dto.ResPortFolioDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@Data
@AllArgsConstructor
public class ResBookmark {
    List<ResBlogDTO> resBlogDTOList;
    List<ResPortFolioDTO> resPortFolioDTOList;
    List<ResProjectDto> resProjectDtoList;
}
