package com.pj.portfoliosite.portfoliosite.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetTokenResponseDto {
    private String resetToken;
    private Long expiresIn; // 초 단위 (예: 600 = 10분)
}
