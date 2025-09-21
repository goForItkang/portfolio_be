package com.pj.portfoliosite.portfoliosite.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserDeleteDto {
    private String password;          // 본인 확인용 비밀번호
    private String verificationCode;  // 이메일 인증 코드
}