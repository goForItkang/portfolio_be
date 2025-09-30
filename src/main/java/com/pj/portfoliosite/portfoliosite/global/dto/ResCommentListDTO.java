package com.pj.portfoliosite.portfoliosite.global.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

// 포트 폴리오 및 commentList
@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class ResCommentListDTO {
    private Long id;
    private String comment;
    private Long parentId;
    private Long userId;
    private String userProfileURL;
    private String userWriteName;
    private boolean isOwner;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private List<ResCommentListDTO> replies; // 대댓글 리스트
}
