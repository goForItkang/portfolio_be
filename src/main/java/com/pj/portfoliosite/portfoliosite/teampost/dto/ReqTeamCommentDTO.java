package com.pj.portfoliosite.portfoliosite.teampost.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReqTeamCommentDTO {
    private String comment;
    private Long parentCommentId; // 대댓글용
}