package com.pj.portfoliosite.portfoliosite.global.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TeamPostChat {
    @Id
    @GeneratedValue
    private Long id;

    private String senderId;
    @Lob
    private String message;

    private LocalDateTime sentAt;

    @ManyToOne
    @JoinColumn(name = "team_post_id")
    private TeamPost teamPost;

}
