package com.pj.portfoliosite.portfoliosite.service;

import com.pj.portfoliosite.portfoliosite.global.dto.LoginRequestDto;
import com.pj.portfoliosite.portfoliosite.global.dto.LoginResponseDto;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public LoginResponseDto login(LoginRequestDto request) {

        return new LoginResponseDto(true, "로그인 요청이 정상적으로 처리되었습니다.");
    }
}