package com.pj.portfoliosite.portfoliosite.global.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// 포트 폴리오 및 commentList
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResCommentListDTO {
    private Long id;
    private String comment;
    private Long parentId;
    private Long userId;
    private String userProfileURL;
    private String userWriteName;
    private List<ResCommentListDTO> replies; // 대댓글 리스트
}
