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
import com.pj.portfoliosite.portfoliosite.user.dto.PasswordResetRequestDto;
import com.pj.portfoliosite.portfoliosite.user.dto.PasswordResetDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

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

        // 중복 이메일 확인
        if (userRepository.findByEmail(email).isPresent()) {
            return new DataResponse<>(400, "이미 존재하는 이메일입니다", null);
        }

        // 이메일 인증 여부 확인
        if (!emailUtil.isEmailVerified(email)) {
            return new DataResponse<>(400, "이메일 인증이 필요합니다.", null);
        }

        // 사용자 생성 및 저장
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        // 추가 정보 설정
        user.setName(request.getName());
        user.setNickname(request.getNickname());
        user.setJob(request.getJob());

        // 생년월일 변환 (String -> LocalDateTime)
        if (request.getBirthDate() != null && !request.getBirthDate().isEmpty()) {
            try {
                LocalDate birthDate = LocalDate.parse(request.getBirthDate());
                user.setBirthday(birthDate.atStartOfDay());
            } catch (Exception e) {
                return new DataResponse<>(400, "올바르지 않은 생년월일 형식입니다. (YYYY-MM-DD)", null);
            }
        }

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

    // 비밀번호 유효성 검증 메서드
    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 8 || password.length() > 16) {
            return false;
        }

        boolean hasUpperCase = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLowerCase = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecialChar = password.chars().anyMatch(ch ->
                "!@#$%^&*()_+-=[]{}|;:,.<>?".indexOf(ch) >= 0);

        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar;
    }

    // 비밀번호 재설정 이메일 발송
    public DataResponse<String> sendPasswordResetEmail(String email) {
        // 사용자 존재 여부 확인
        if (!userRepository.findByEmail(email).isPresent()) {
            return new DataResponse<>(404, "등록되지 않은 이메일입니다.", null);
        }

        boolean success = emailUtil.sendPasswordResetEmail(email);

        if (success) {
            return new DataResponse<>(200, "비밀번호 재설정 코드가 발송되었습니다.", null);
        } else {
            return new DataResponse<>(500, "이메일 발송에 실패했습니다.", null);
        }
    }

    // 비밀번호 재설정 처리
    public DataResponse<String> resetPassword(String email, String newPassword, String verificationCode) {
        // 이메일 존재 여부 확인
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent()) {
            return new DataResponse<>(404, "등록되지 않은 이메일입니다.", null);
        }

        // 인증 코드 검증
        if (!emailUtil.verifyCode(email, verificationCode)) {
            return new DataResponse<>(400, "잘못된 인증 코드이거나 만료되었습니다.", null);
        }

        // 비밀번호 유효성 검증
        if (!isValidPassword(newPassword)) {
            return new DataResponse<>(400,
                    "비밀번호는 8-16자, 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다.", null);
        }

        // 비밀번호 변경
        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // 인증 정보 정리
        emailUtil.removeVerifiedEmail(email);

        return new DataResponse<>(200, "비밀번호가 성공적으로 변경되었습니다.", null);
    }

    // 로그아웃
    @Transactional
    public DataResponse<String> logout(String email) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(email);
            
            if (userOpt.isEmpty()) {
                return new DataResponse<>(404, "사용자를 찾을 수 없습니다.", null);
            }
            
            User user = userOpt.get();
            
            // RefreshToken 삭제
            user.setRefreshToken(null);
            user.setRefreshTokenExpiry(null);
            userRepository.save(user);
            
            return new DataResponse<>(200, "로그아웃이 완료되었습니다.", null);
            
        } catch (Exception e) {
            return new DataResponse<>(500, "로그아웃 처리 중 오류가 발생했습니다.", null);
        }
    }

    // 회원탈퇴용 이메일 인증 발송
    public DataResponse<String> sendDeleteAccountVerificationEmail(String email) {
        // 사용자 존재 여부 확인
        if (!userRepository.findByEmail(email).isPresent()) {
            return new DataResponse<>(404, "등록되지 않은 이메일입니다.", null);
        }

        boolean success = emailUtil.sendDeleteAccountEmail(email);

        if (success) {
            return new DataResponse<>(200, "회원탈퇴 인증 코드가 발송되었습니다.", null);
        } else {
            return new DataResponse<>(500, "이메일 발송에 실패했습니다.", null);
        }
    }

    // 회원탈퇴
    @Transactional
    public DataResponse<String> deleteUser(String email, String password, String verificationCode) {
        // 사용자 존재 여부 확인
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent()) {
            return new DataResponse<>(404, "사용자를 찾을 수 없습니다.", null);
        }

        User user = userOpt.get();

        // 비밀번호 확인
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return new DataResponse<>(400, "비밀번호가 일치하지 않습니다.", null);
        }

        // 이메일 인증 코드 확인
        if (!emailUtil.verifyCode(email, verificationCode)) {
            return new DataResponse<>(400, "잘못된 인증 코드이거나 만료되었습니다.", null);
        }

        try {
            // 사용자 삭제
            userRepository.delete(user);

            // 인증 정보 정리
            emailUtil.removeVerifiedEmail(email);

            return new DataResponse<>(200, "회원탈퇴가 완료되었습니다.", null);

        } catch (Exception e) {
            return new DataResponse<>(500, "회원탈퇴 처리 중 오류가 발생했습니다.", null);
        }
    }
}