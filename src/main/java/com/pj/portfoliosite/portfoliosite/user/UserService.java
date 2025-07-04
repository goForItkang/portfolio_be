package com.pj.portfoliosite.portfoliosite.user;

import com.pj.portfoliosite.portfoliosite.config.JwtTokenProvider;
import com.pj.portfoliosite.portfoliosite.global.dto.LoginRequestDto;
import com.pj.portfoliosite.portfoliosite.global.dto.LoginResponseDto;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public LoginResponseDto login(LoginRequestDto request) {

        String email = request.getEmail();
        String password = request.getPassword();


        if ("portfolio@naver.com".equals(email) && "1111".equals(password)) {
            String token = JwtTokenProvider.createToken(email);
            return new LoginResponseDto(true, "로그인 성공", token);
        }

        return new LoginResponseDto(false, "로그인 실패", null);
    }
}