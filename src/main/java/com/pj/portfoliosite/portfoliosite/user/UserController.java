package com.pj.portfoliosite.portfoliosite.user;

import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import com.pj.portfoliosite.portfoliosite.global.dto.LoginRequestDto;
import com.pj.portfoliosite.portfoliosite.global.dto.LoginResponseDto;
import com.pj.portfoliosite.portfoliosite.global.entity.User;
import com.pj.portfoliosite.portfoliosite.global.exception.CustomException;
import com.pj.portfoliosite.portfoliosite.global.errocode.UserErrorCode;
import com.pj.portfoliosite.portfoliosite.user.dto.ReqLoginDTO;
import com.pj.portfoliosite.portfoliosite.util.OAuthUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import com.pj.portfoliosite.portfoliosite.util.EmailUtil; // 추가

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.pj.portfoliosite.portfoliosite.user.dto.PasswordResetRequestDto;
import com.pj.portfoliosite.portfoliosite.user.dto.PasswordResetDto;
import jakarta.validation.Valid;
import com.pj.portfoliosite.portfoliosite.user.dto.UserDeleteDto;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.net.URI;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/user")

public class UserController {

    private final UserService userService;
    private final EmailUtil emailUtil; // 추가

    @Autowired
    private OAuthUtil oAuthUtil;

    public UserController(UserService userService, EmailUtil emailUtil) {
        this.userService = userService;
        this.emailUtil = emailUtil;
    }

    /**
     * 로그인 (강화된 디버깅 버전)
     */
    @PostMapping("/login")

    public DataResponse<LoginResponseDto> login(@RequestBody ReqLoginDTO requestDto) {
        try {
            log.info("=== 로그인 컨트롤러 요청 수신 ===");
            log.info("요청 데이터: email={}, password={}***",
                    requestDto != null ? requestDto.getEmail() : "null",
                    requestDto != null && requestDto.getPassword() != null ? requestDto.getPassword().substring(0, Math.min(3, requestDto.getPassword().length())) : "null");

            if (requestDto == null) {
                log.error("로그인 요청 데이터가 null");
                return new DataResponse<>(400, "요청 데이터가 비어있습니다", null);
            }

            log.info("서비스 로그인 메서드 호출 시작...");
            LoginResponseDto responseDto = userService.login(requestDto);

            if (responseDto == null) {
                log.error("서비스에서 null 응답 반환");
                return new DataResponse<>(500, "로그인 처리 중 오류가 발생했습니다", null);
            }

            log.info("서비스 응답: success={}, message={}, token={}",
                    responseDto.isSuccess(),
                    responseDto.getMessage(),
                    responseDto.getToken() != null ? "[토큰 있음]" : "null");

            int statusCode = responseDto.isSuccess() ? 200 : 401;
            return new DataResponse<>(statusCode, "Login processed", responseDto);

        } catch (Exception e) {
            log.error("로그인 컨트롤러 오류: {}", e.getMessage(), e);
            return new DataResponse<>(500, "로그인 처리 중 오류가 발생했습니다", null);
        }

    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public DataResponse<String> logout(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return new DataResponse<>(401, "로그인이 필요합니다.", null);
        }

        String email = authentication.getName();
        return userService.logout(email);
    }

    /**
     * 사용자 이메일 복호화 (개발/디버깅용)
     */
    @GetMapping("/email/{id}")
    public DataResponse<String> getDecryptedEmail(@PathVariable Long id) {
        try {
            String decryptedEmail = userService.getDecryptedEmail(id);
            return new DataResponse<>(200, "복호화된 이메일", decryptedEmail);
        } catch (Exception e) {
            log.error("이메일 복호화 실패: {}", e.getMessage());
            return new DataResponse<>(500, "이메일 복호화에 실패했습니다.", null);
        }
    }

    /**
     * 회원가입
     */
    @PostMapping("/register")
    public DataResponse<String> register(@RequestBody LoginRequestDto requestDto) {
        try {
            return userService.register(requestDto);
        } catch (Exception e) {
            log.error("회원가입 컨트롤러 오류: {}", e.getMessage());
            return new DataResponse<>(500, "회원가입 처리 중 오류가 발생했습니다.", null);
        }
    }

    /**
     * 이메일 인증 코드 발송 (별칭 엔드포인트)
     */
    @PostMapping("/send-verification-email")
    public DataResponse<String> sendVerificationEmailAlias(@RequestBody Map<String, String> request) {
        log.info("=== 이메일 인증 코드 발송 요청 수신 (Alias) ===");
        return sendVerificationEmail(request); // 기존 메서드 재사용
    }

    /**
     * 이메일 인증 코드 발송
     */
    @PostMapping("/send-verification")
    public DataResponse<String> sendVerificationEmail(@RequestBody Map<String, String> request) {
        try {
            log.info("=== 이메일 인증 코드 발송 요청 수신 ===");
            String email = request.get("email");
            log.info("요청 데이터: email={}", email != null ? email.substring(0, Math.min(3, email.length())) + "***" : "null");
            if (email == null || email.trim().isEmpty()) {
                log.warn("이메일 인증 요청 - 이메일 누락");
                return new DataResponse<>(400, "이메일 주소가 필요합니다.", null);
            }

            log.info("서비스 메서드 호출 시작: sendVerificationEmail");
            DataResponse<String> result = userService.sendVerificationEmail(email);

            log.info("서비스 응답: status={}, message={}", result.getStatus(), result.getMessage());

            return result;
        } catch (Exception e) {
            log.error("인증 이메일 발송 컨트롤러 오류: {}", e.getMessage());
            return new DataResponse<>(500, "인증 이메일 발송 중 오류가 발생했습니다.", null);
        }
    }
    @PostMapping("/verify-email")
    public DataResponse<String> verifyEmail(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String verificationCode = request.get("verificationCode");

            if (email == null || verificationCode == null) {
                return new DataResponse<>(400, "이메일과 인증 코드가 필요합니다.", null);
            }

            return userService.verifyEmail(email, verificationCode);
        } catch (Exception e) {
            log.error("이메일 인증 확인 컨트롤러 오류: {}", e.getMessage());
            return new DataResponse<>(500, "이메일 인증 확인 중 오류가 발생했습니다.", null);
        }
    }

    /**
     * OAuth 인증 URL 생성
     */
    @GetMapping("/oauth/{provider}/url")
    public DataResponse<String> getOAuthUrl(@PathVariable String provider) {
        try {
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
        } catch (Exception e) {
            log.error("OAuth URL 생성 실패: {}", e.getMessage());
            return new DataResponse<>(500, "OAuth URL 생성에 실패했습니다.", null);
        }
    }

    /**
     * OAuth 콜백 처리
     */
    @GetMapping("/oauth/{provider}/callback")
    public ResponseEntity<String> handleOAuthCallback(
            @PathVariable String provider,
            @RequestParam String code) {
        try {
            DataResponse<LoginResponseDto> result = userService.processOAuthLogin(provider, code);

            String redirectUrl = "http://localhost:3000/auth/success" +
                    "?token=" + result.getData().getToken() +
                    "&provider=" + provider;

            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(redirectUrl))
                    .build();
        } catch (Exception e) {
            log.error("OAuth 콜백 처리 실패: {}", e.getMessage());
            String errorUrl = "http://localhost:3000/auth/error?message=" + e.getMessage();
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(errorUrl))
                    .build();
        }
    }

    /**
     * 현재 사용자 정보 조회
     */
    @GetMapping("/me")
    public DataResponse<User> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(UserErrorCode.USER_NOT_FOUND);
        }

        try {
            String email = authentication.getName();
            User user = userService.getSafeUserInfo(email); // 민감한 정보 제거된 사용자 정보 반환

            return new DataResponse<>(200, "사용자 정보 조회 성공", user);
        } catch (Exception e) {
            log.error("사용자 정보 조회 실패: {}", e.getMessage());
            return new DataResponse<>(500, "사용자 정보 조회에 실패했습니다.", null);
        }
    }

    /**
     * 비밀번호 재설정 요청 (1단계)
     */
    @PostMapping("/password-reset-request")
    public DataResponse<String> requestPasswordReset(@RequestBody PasswordResetRequestDto request) {
        try {
            return userService.sendPasswordResetEmail(request.getEmail());
        } catch (Exception e) {
            log.error("비밀번호 재설정 요청 컨트롤러 오류: {}", e.getMessage());
            return new DataResponse<>(500, "비밀번호 재설정 요청 처리 중 오류가 발생했습니다.", null);
        }
    }

    /**
     * 비밀번호 재설정 실행 (2단계)
     */
    @PostMapping("/password-reset")
    public DataResponse<String> resetPassword(@Valid @RequestBody PasswordResetDto request) {
        try {
            return userService.resetPassword(request.getEmail(), request.getNewPassword(), request.getVerificationCode());
        } catch (Exception e) {
            log.error("비밀번호 재설정 컨트롤러 오류: {}", e.getMessage());
            return new DataResponse<>(500, "비밀번호 재설정 처리 중 오류가 발생했습니다.", null);
        }
    }

    /**
     * 회원탈퇴용 이메일 인증 코드 발송
     */
    @PostMapping("/send-delete-verification")
    public DataResponse<String> sendDeleteAccountVerification(@RequestBody Map<String, String> request) {
        try {
            log.info("=== 회원탈퇴 인증 이메일 발송 요청 수신 ===");
            String email = request.get("email");
            log.info("요청 데이터: email={}", email != null ? email.substring(0, Math.min(3, email.length())) + "***" : "null");

            if (email == null || email.trim().isEmpty()) {
                log.warn("회원탈퇴 인증 요청 - 이메일 누락");
                return new DataResponse<>(400, "이메일 주소가 필요합니다.", null);
            }

            log.info("서비스 메서드 호출 시작: sendDeleteAccountVerificationEmail");
            DataResponse<String> result = userService.sendDeleteAccountVerificationEmail(email);

            log.info("서비스 응답: status={}, message={}", result.getStatus(), result.getMessage());

            return result;
        } catch (Exception e) {
            log.error("회원탈퇴 인증 이메일 발송 컨트롤러 오류: {}", e.getMessage(), e);
            return new DataResponse<>(500, "회원탈퇴 인증 이메일 발송 중 오류가 발생했습니다.", null);
        }
    }

    /**
     * 디버깅용: 저장된 인증 코드 확인
     */
    @GetMapping("/debug/verification-code/{email}")
    public DataResponse<String> getStoredVerificationCode(@PathVariable String email) {
        try {
            String storedCode = emailUtil.getStoredVerificationCode(email);
            if (storedCode == null) {
                return new DataResponse<>(404, "해당 이메일에 대한 인증 코드가 없습니다.", null);
            }
            return new DataResponse<>(200, "저장된 인증 코드", storedCode);
        } catch (Exception e) {
            log.error("인증 코드 확인 오류: {}", e.getMessage());
            return new DataResponse<>(500, "인증 코드 확인에 실패했습니다.", null);
        }
    }

    /**
     * 회원탈퇴
     */
    @DeleteMapping("/delete")
    public DataResponse<String> deleteUser(@RequestBody UserDeleteDto request, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return new DataResponse<>(401, "로그인이 필요합니다.", null);
        }

        try {
            String email = authentication.getName();
            return userService.deleteUser(email, request.getPassword(), request.getVerificationCode());
        } catch (Exception e) {
            log.error("회원탈퇴 컨트롤러 오류: {}", e.getMessage());
            return new DataResponse<>(500, "회원탈퇴 처리 중 오류가 발생했습니다.", null);
        }
    }
    @PatchMapping(value = "/profile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DataResponse> updateProfile(
            MultipartFile profile
    ) throws IOException {
        DataResponse response = new DataResponse();
        response.setStatus(200);
        response.setMessage("success");
        userService.profileUpdate(profile);
        return ResponseEntity.ok(response);
    }
}
