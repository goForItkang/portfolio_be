package com.pj.portfoliosite.portfoliosite.user;

import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import com.pj.portfoliosite.portfoliosite.global.dto.LoginRequestDto;
import com.pj.portfoliosite.portfoliosite.global.dto.LoginResponseDto;
import com.pj.portfoliosite.portfoliosite.global.entity.User;
import com.pj.portfoliosite.portfoliosite.global.exception.CustomException;
import com.pj.portfoliosite.portfoliosite.global.errocode.UserErrorCode;
import com.pj.portfoliosite.portfoliosite.user.dto.ReqLoginDTO;
import com.pj.portfoliosite.portfoliosite.util.OAuthUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.pj.portfoliosite.portfoliosite.user.dto.PasswordResetRequestDto;
import com.pj.portfoliosite.portfoliosite.user.dto.PasswordResetDto;
import jakarta.validation.Valid;
import com.pj.portfoliosite.portfoliosite.user.dto.UserDeleteDto;
import org.springframework.security.core.Authentication;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {

    private final UserService userService;
    @Autowired
    private OAuthUtil oAuthUtil;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public DataResponse<LoginResponseDto> login(
            @RequestBody ReqLoginDTO requestDto
    ) {
        log.warn("Login Request: {}", requestDto);
        LoginResponseDto responseDto = userService.login(requestDto);
        return new DataResponse<>(200, "Login processed", responseDto);
    }

    @PostMapping("/logout")
    public DataResponse<String> logout(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return new DataResponse<>(401, "로그인이 필요합니다.", null);
        }
        
        String email = authentication.getName();
        return userService.logout(email);
    }

    @GetMapping("/email/{id}")
    public DataResponse<String> getDecryptedEmail(@PathVariable Long id) {
        String decryptedEmail = userService.getDecryptedEmail(id);
        return new DataResponse<>(200, "복호화된 이메일", decryptedEmail);
    }

    @PostMapping("/register")
    public DataResponse<String> register(@RequestBody LoginRequestDto requestDto) {
        return userService.register(requestDto);
    }

    @PostMapping("/send-verification")
    public DataResponse<String> sendVerificationEmail(@RequestBody Map<String, String> request) {
        return userService.sendVerificationEmail(request.get("email"));
    }

    @PostMapping("/verify-email")
    public DataResponse<String> verifyEmail(@RequestBody Map<String, String> request) {
        return userService.verifyEmail(request.get("email"), request.get("verificationCode"));
    }

    @GetMapping("/oauth/{provider}/url")
    public DataResponse<String> getOAuthUrl(@PathVariable String provider) {
        String authUrl;
        switch (provider.toLowerCase()) {
            case "github":
                authUrl = oAuthUtil.getGitHubAuthUrl();
                break;
            case "google":
                authUrl = oAuthUtil.getGoogleAuthUrl();
                break;
            default:
                throw new CustomException(UserErrorCode.INVALID_PROVIDER);
        }
        return new DataResponse<>(200, "OAuth URL 생성 성공", authUrl);
    }

    @GetMapping("/oauth/{provider}/callback")
    public ResponseEntity<String> handleOAuthCallback(
            @PathVariable String provider,
            @RequestParam String code) {

        DataResponse<LoginResponseDto> result = userService.processOAuthLogin(provider, code);

        String redirectUrl = "http://localhost:3000/auth/success" +
                "?token=" + result.getData().getToken() +
                "&provider=" + provider;

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(redirectUrl))
                .build();
    }

    @GetMapping("/me")
    public DataResponse<User> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(UserErrorCode.USER_NOT_FOUND);
        }

        String email = authentication.getName();
        User user = userService.getUserByEmail(email);

        user.setPassword(null);
        user.setRefreshToken(null);

        return new DataResponse<>(200, "사용자 정보 조회 성공", user);
    }

    // 비밀번호 재설정 요청 (1단계)
    @PostMapping("/password-reset-request")
    public DataResponse<String> requestPasswordReset(@RequestBody PasswordResetRequestDto request) {
        return userService.sendPasswordResetEmail(request.getEmail());
    }

    // 비밀번호 재설정 실행 (2단계)
    @PostMapping("/password-reset")
    public DataResponse<String> resetPassword(@Valid @RequestBody PasswordResetDto request) {
        return userService.resetPassword(request.getEmail(), request.getNewPassword(), request.getVerificationCode());
    }

    // 회원탈퇴용 이메일 인증 코드 발송
    @PostMapping("/send-delete-verification")
    public DataResponse<String> sendDeleteAccountVerification(@RequestBody Map<String, String> request) {
        return userService.sendDeleteAccountVerificationEmail(request.get("email"));
    }

    // 회원탈퇴
    @DeleteMapping("/delete")
    public DataResponse<String> deleteUser(@RequestBody UserDeleteDto request, Authentication authentication) {
        String email = authentication.getName();
        return userService.deleteUser(email, request.getPassword(), request.getVerificationCode());
    }
}