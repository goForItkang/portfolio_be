package com.pj.portfoliosite.portfoliosite.teampost.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReqTeamCommentDTO {
    private String comment;
    
    @JsonProperty("parent_id")
    private Long parentCommentId; // 대댓글용
}