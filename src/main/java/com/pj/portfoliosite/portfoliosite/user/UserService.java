package com.pj.portfoliosite.portfoliosite.user;

import com.pj.portfoliosite.portfoliosite.config.JwtTokenProvider;
import com.pj.portfoliosite.portfoliosite.global.dto.LoginRequestDto;
import com.pj.portfoliosite.portfoliosite.global.dto.LoginResponseDto;
import com.pj.portfoliosite.portfoliosite.global.entity.User;
import com.pj.portfoliosite.portfoliosite.util.AESUtil;
import com.pj.portfoliosite.portfoliosite.util.EmailUtil;
import com.pj.portfoliosite.portfoliosite.util.OAuthUtil;
import com.pj.portfoliosite.portfoliosite.global.exception.CustomException;
import com.pj.portfoliosite.portfoliosite.global.errocode.UserErrorCode;
import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AESUtil aesUtil;
    private final PasswordEncoder passwordEncoder;
    private final EmailUtil emailUtil;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    private OAuthUtil oAuthUtil;

    public LoginResponseDto login(LoginRequestDto request) {
        String inputEmail = request.getEmail();
        String inputPassword = request.getPassword();

        User user = userRepository.findByEmail(inputEmail).orElse(null);

        if (user == null || !passwordEncoder.matches(inputPassword, user.getPassword())) {
            return new LoginResponseDto(false, "로그인 실패", null);
        }

        String token = jwtTokenProvider.createToken(inputEmail);
        String refreshToken = jwtTokenProvider.createRefreshToken(inputEmail);

        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiry(LocalDateTime.now().plusDays(7));
        userRepository.save(user);

        return new LoginResponseDto(true, "로그인 성공", token);
    }

    public String getDecryptedEmail(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return aesUtil.decode(user.getEmail());
    }

    public DataResponse<String> sendVerificationEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            return new DataResponse<>(400, "이미 가입된 이메일입니다.", null);
        }

        boolean success = emailUtil.sendVerificationEmail(email);

        if (success) {
            return new DataResponse<>(200, "인증 이메일이 발송되었습니다.", null);
        } else {
            return new DataResponse<>(500, "이메일 발송에 실패했습니다.", null);
        }
    }

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

        if (userRepository.findByEmail(email).isPresent()) {
            return new DataResponse<>(400, "이미 존재하는 이메일입니다", null);
        }

        if (!emailUtil.isEmailVerified(email)) {
            return new DataResponse<>(400, "이메일 인증이 필요합니다.", null);
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setCreateAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        userRepository.save(user);
        emailUtil.removeVerifiedEmail(email);

        return new DataResponse<>(200, "회원가입 성공", "사용자가 생성되었습니다");
    }

    public DataResponse<LoginResponseDto> processOAuthLogin(String provider, String code) {
        try {
            Map<String, String> userInfo;
            String accessToken;

            switch (provider.toLowerCase()) {
                case "github":
                    accessToken = oAuthUtil.getGitHubAccessToken(code);
                    userInfo = oAuthUtil.getGitHubUserInfo(accessToken);
                    break;
                case "google":
                    accessToken = oAuthUtil.getGoogleAccessToken(code);
                    userInfo = oAuthUtil.getGoogleUserInfo(accessToken);
                    break;
                default:
                    throw new CustomException(UserErrorCode.INVALID_PROVIDER);
            }

            String email = userInfo != null ? userInfo.get("email") : null;
            String name = userInfo != null ? userInfo.get("name") : null;
            String profile = userInfo != null ? userInfo.get("profile") : null;
            String providerType = userInfo != null ? userInfo.get("provider") : null;

            if (email == null || email.isEmpty()) {
                throw new CustomException(UserErrorCode.OAUTH_LOGIN_FAILED);
            }

            Optional<User> existingUser = userRepository.findByEmail(email);
            User user;

            if (existingUser.isPresent()) {
                user = existingUser.get();
            } else {
                user = new User();
                user.setEmail(email);
                user.setName(name);
                user.setProfile(profile);
                user.setProvider(providerType);
                user.setPassword(passwordEncoder.encode("OAUTH_USER_" + System.currentTimeMillis()));
                user.setCreateAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                userRepository.save(user);
            }

            String jwtToken = jwtTokenProvider.createToken(email);
            String refreshToken = jwtTokenProvider.createRefreshToken(email);

            user.setRefreshToken(refreshToken);
            user.setRefreshTokenExpiry(LocalDateTime.now().plusDays(7));
            userRepository.save(user);

            LoginResponseDto responseDto = new LoginResponseDto(true, "OAuth 로그인 성공", jwtToken);
            return new DataResponse<>(200, "OAuth 로그인 성공", responseDto);

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(UserErrorCode.OAUTH_LOGIN_FAILED);
        }
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
    }
}