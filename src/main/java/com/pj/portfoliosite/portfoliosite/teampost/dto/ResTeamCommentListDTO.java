package com.pj.portfoliosite.portfoliosite.teampost.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResTeamCommentListDTO {
    private Long id;
    private String comment;
    private Long parentId;
    private Long userId;
    private String userProfileURL;
    private String userWriteName;
    private boolean isOwner;
    private List<ResTeamCommentListDTO> replies;
}