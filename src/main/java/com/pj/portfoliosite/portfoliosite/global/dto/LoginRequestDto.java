package com.pj.portfoliosite.portfoliosite.global.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@NoArgsConstructor
@Setter
public class LoginRequestDto {
    private String email;
    private String password;

    private String name;
    private String birthDate;
    private String nickname;
    private String job;
    private boolean agreeTerms;
    private String verificationCode; // 추가: 회원가입 시 이메일 인증 코드

}