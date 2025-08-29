package com.pj.portfoliosite.portfoliosite.user;

import ch.qos.logback.classic.Logger;
import com.pj.portfoliosite.portfoliosite.config.JwtTokenProvider;
import com.pj.portfoliosite.portfoliosite.global.dto.LoginRequestDto;
import com.pj.portfoliosite.portfoliosite.global.dto.LoginResponseDto;
import com.pj.portfoliosite.portfoliosite.global.entity.RefreshToken;
import com.pj.portfoliosite.portfoliosite.global.entity.User;
import com.pj.portfoliosite.portfoliosite.util.AESUtil;
import com.pj.portfoliosite.portfoliosite.util.EmailUtil; // EmailUtil import 추가
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import java.time.format.DateTimeFormatter;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final AESUtil aesUtil;
    private final PasswordEncoder passwordEncoder;
    private final EmailUtil emailUtil; // EmailUtil 의존성 추가

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

    public String getDecryptedEmail(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return aesUtil.decode(user.getEmail());
    }

    /* 이메일 인증 코드 발송 */
    public DataResponse<String> sendVerificationEmail(String email) {
        // 이미 가입된 이메일인지 확인
        if (userRepository.findByEmail(email).isPresent()) {
            return new DataResponse<>(400, "이미 가입된 이메일입니다.", null);
        }

        // EmailUtil을 사용해서 인증 이메일 발송
        boolean success = emailUtil.sendVerificationEmail(email);

        if (success) {
            return new DataResponse<>(200, "인증 이메일이 발송되었습니다.", null);
        } else {
            return new DataResponse<>(500, "이메일 발송에 실패했습니다.", null);
        }
    }

    /* 이메일 인증 코드 확인 */
    public DataResponse<String> verifyEmail(String email, String code) {
        boolean isVerified = emailUtil.verifyCode(email, code);

        if (isVerified) {
            return new DataResponse<>(200, "이메일 인증이 완료되었습니다.", null);
        } else {
            return new DataResponse<>(400, "잘못된 인증 코드이거나 만료되었습니다.", null);
        }
    }

    public DataResponse<String> register(LoginRequestDto request) {
        String email = request.getEmail();
        String password = request.getPassword();

        // 중복 이메일 확인
        if (userRepository.findByEmail(email).isPresent()) {
            return new DataResponse<>(400, "이미 존재하는 이메일입니다", null);
        }

        // 이메일 인증 여부 확인 (새로 추가된 중요한 부분!)
        if (!emailUtil.isEmailVerified(email)) {
            return new DataResponse<>(400, "이메일 인증이 필요합니다.", null);
        }

        // 사용자 생성 및 저장
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setCreateAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        userRepository.save(user);

        // 회원가입 완료 후 인증 정보 메모리에서 제거 (메모리 관리)
        emailUtil.removeVerifiedEmail(email);

        return new DataResponse<>(200, "회원가입 성공", "사용자가 생성되었습니다");
    }
}