package com.pj.portfoliosite.portfoliosite.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * TeamPost 북마크용 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResTeamPostBookmarkDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private String type;

    public ResTeamPostBookmarkDTO(Long id, String title, String content, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = content != null ? content.substring(0, Math.min(100, content.length())) : null;
        this.createdAt = createdAt;
        this.type = "teampost";
    }
}
