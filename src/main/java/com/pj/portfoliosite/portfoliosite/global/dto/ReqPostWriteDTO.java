package com.pj.portfoliosite.portfoliosite.global.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReqPostWriteDTO {
    private String title;
    private String content;
    private List<RecruitRoleDto> recruitList;

}
