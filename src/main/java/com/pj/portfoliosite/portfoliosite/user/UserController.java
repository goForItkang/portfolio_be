package com.pj.portfoliosite.portfoliosite.user;

import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import com.pj.portfoliosite.portfoliosite.global.dto.LoginRequestDto;
import com.pj.portfoliosite.portfoliosite.global.dto.LoginResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public DataResponse<LoginResponseDto> login(@RequestBody LoginRequestDto requestDto) {
        LoginResponseDto responseDto = userService.login(requestDto);
        return new DataResponse<>(200, "Login processed", responseDto);
    }

    @GetMapping("/email/{id}")
    public DataResponse<String> getDecryptedEmail(
            @PathVariable Long id) {
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

}