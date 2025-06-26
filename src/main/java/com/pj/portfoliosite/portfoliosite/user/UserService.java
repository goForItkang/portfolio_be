package com.pj.portfoliosite.portfoliosite.user;

import com.pj.portfoliosite.portfoliosite.global.dto.LoginRequestDto;
import com.pj.portfoliosite.portfoliosite.global.dto.LoginResponseDto;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public LoginResponseDto login(LoginRequestDto request) {

        return new LoginResponseDto(true, "로그인 요청이 정상적으로 처리되었습니다.");
    }
}