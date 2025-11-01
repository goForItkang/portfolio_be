package com.pj.portfoliosite.portfoliosite.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VerifyPasswordResetCodeDto {
    private String email;
    private String verificationCode;
}
