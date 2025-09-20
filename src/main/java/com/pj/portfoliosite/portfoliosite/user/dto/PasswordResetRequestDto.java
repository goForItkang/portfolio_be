package com.pj.portfoliosite.portfoliosite.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PasswordResetRequestDto {
    private String email;
}