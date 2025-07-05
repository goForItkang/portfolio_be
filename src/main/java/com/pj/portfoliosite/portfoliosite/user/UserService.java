package com.pj.portfoliosite.portfoliosite.user;

import com.pj.portfoliosite.portfoliosite.config.JwtTokenProvider;
import com.pj.portfoliosite.portfoliosite.global.dto.LoginRequestDto;
import com.pj.portfoliosite.portfoliosite.global.dto.LoginResponseDto;
import com.pj.portfoliosite.portfoliosite.global.entity.RefreshToken;
import com.pj.portfoliosite.portfoliosite.global.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final RefreshTokenRepository refreshTokenRepository;

    public LoginResponseDto login(LoginRequestDto request) {

        String email = request.getEmail();
        String password = request.getPassword();


        if ("portfolio@naver.com".equals(email) && "1111".equals(password)) {
            String token = JwtTokenProvider.createToken(email);

            String refreshToken = JwtTokenProvider.createRefreshToken(email);

            RefreshToken entity = new RefreshToken();
            entity.setRefreshToken(refreshToken);
            entity.setExpiryDate(LocalDateTime.now().plusDays(7));
            refreshTokenRepository.save(entity);

            return new LoginResponseDto(true, "로그인 성공", token);
        }

        return new LoginResponseDto(false, "로그인 실패", null);
    }
}