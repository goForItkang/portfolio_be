package com.pj.portfoliosite.portfoliosite.user;

import ch.qos.logback.classic.Logger;
import com.pj.portfoliosite.portfoliosite.config.JwtTokenProvider;
import com.pj.portfoliosite.portfoliosite.global.dto.LoginRequestDto;
import com.pj.portfoliosite.portfoliosite.global.dto.LoginResponseDto;
import com.pj.portfoliosite.portfoliosite.global.entity.RefreshToken;
import com.pj.portfoliosite.portfoliosite.global.entity.User;
import com.pj.portfoliosite.portfoliosite.util.AESUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final AESUtil aesUtil;
    private final PasswordEncoder passwordEncoder;


    public LoginResponseDto login(LoginRequestDto request) {
        String inputEmail = request.getEmail();
        String inputPassword = request.getPassword();

        User user = userRepository.findByEmail(inputEmail).orElse(null);

        if (user == null || !passwordEncoder.matches(inputPassword, user.getPassword())) {
            return new LoginResponseDto(false, "로그인 실패", null);
        }


        String token = JwtTokenProvider.createToken(inputEmail);
        String refreshToken = JwtTokenProvider.createRefreshToken(inputEmail);


        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiry(LocalDateTime.now().plusDays(7));
        userRepository.save(user);

        return new LoginResponseDto(true, "로그인 성공", token);
    }

    public  String getDecryptedEmail(Long userId){
        User user = userRepository.findById(userId).orElseThrow();
        return aesUtil.decode(user.getEmail());
    }

}