package com.pj.portfoliosite.portfoliosite.user;

import com.pj.portfoliosite.portfoliosite.config.JwtTokenProvider;
import com.pj.portfoliosite.portfoliosite.global.dto.LoginRequestDto;
import com.pj.portfoliosite.portfoliosite.global.dto.LoginResponseDto;
import com.pj.portfoliosite.portfoliosite.global.entity.User;
import com.pj.portfoliosite.portfoliosite.user.dto.ReqLoginDTO;
import com.pj.portfoliosite.portfoliosite.util.*;
import com.pj.portfoliosite.portfoliosite.global.exception.CustomException;
import com.pj.portfoliosite.portfoliosite.global.errocode.UserErrorCode;
import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PersonalDataUtil personalDataUtil;
    private final PasswordEncoder passwordEncoder;
    private final EmailUtil emailUtil;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    private OAuthUtil oAuthUtil;
    @Autowired
    private ImgUtil imgUtil;
    @Autowired
    private AESUtil aesUtil;

    /**
     * 로그인 처리 (단순화 및 안정화)
     */
    public LoginResponseDto login(ReqLoginDTO request) {
        String inputEmail = request.getEmail();
        String inputPassword = request.getPassword();


        try {
            // 입력값 검증
            if (inputEmail == null || inputEmail.trim().isEmpty()) {
                return new LoginResponseDto(false, "이메일을 입력해주세요", null);
            }

            if (inputPassword == null || inputPassword.trim().isEmpty()) {
                return new LoginResponseDto(false, "비밀번호를 입력해주세요", null);
            }

            User user = findUserByEmailSafely(inputEmail);

            if (user == null) {
                return new LoginResponseDto(false, "이메일 또는 비밀번호가 올바르지 않습니다", null);
            }

            // 비밀번호 검증
            if (!passwordEncoder.matches(inputPassword, user.getPassword())) {
                return new LoginResponseDto(false, "이메일 또는 비밀번호가 올바르지 않습니다", null);
            }


            // JWT 토큰 생성
            String token = jwtTokenProvider.createToken(user);
            String refreshToken = jwtTokenProvider.createRefreshToken(inputEmail);

            // RefreshToken 저장
            user.setRefreshToken(refreshToken);
            user.setRefreshTokenExpiry(LocalDateTime.now().plusDays(7));
            userRepository.save(user);

            return new LoginResponseDto(true, "로그인 성공", token);

        } catch (Exception e) {
            log.error("로그인 처리 중 오류: {}", e.getMessage());
            return new LoginResponseDto(false, "로그인 처리 중 오류가 발생했습니다", null);
        }
    }

    /**
     * 안전한 사용자 검색
     */
    private User findUserByEmailSafely(String email) {
        try {
            List<User> allUsers = userRepository.findAllForMigration();
            
            if (allUsers.isEmpty()) {
                log.warn("데이터베이스에 사용자가 없습니다");
                return null;
            }

            String normalizedEmail = email.trim().toLowerCase();

            for (User user : allUsers) {
                try {
                    String userEmail = user.getEmail();
                    if (userEmail == null || userEmail.trim().isEmpty()) {
                        continue;
                    }

                    // 평문 비교
                    if (normalizedEmail.equals(userEmail.trim().toLowerCase())) {
                        return user;
                    }

                    // 복호화 후 비교
                    try {
                        String decryptedEmail = personalDataUtil.decryptPersonalData(userEmail);
                        if (decryptedEmail != null && !decryptedEmail.equals(userEmail)) {
                            if (normalizedEmail.equals(decryptedEmail.trim().toLowerCase())) {
                                return user;
                            }
                        }
                    } catch (Exception decryptError) {
                        // 복호화 실패는 무시하고 계속
                    }

                } catch (Exception userError) {
                    // 개별 사용자 처리 실패는 무시하고 계속
                }
            }

            return null;

        } catch (Exception e) {
            log.error("사용자 검색 중 오류: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 복호화된 이메일 반환
     */
    public String getDecryptedEmail(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        return user.getEmail();
    }

    /**
     * 이메일 인증 발송
     */
    public DataResponse<String> sendVerificationEmail(String email) {
        try {
            if (isEmailAlreadyExists(email)) {
                return new DataResponse<>(400, "이미 가입된 이메일입니다.", null);
            }

            boolean success = emailUtil.sendVerificationEmail(email);

            if (success) {
                return new DataResponse<>(200, "인증 이메일이 발송되었습니다.", null);
            } else {
                return new DataResponse<>(500, "이메일 발송에 실패했습니다.", null);
            }
        } catch (Exception e) {
            log.error("이메일 인증 발송 실패: {}", e.getMessage());
            return new DataResponse<>(500, "이메일 발송에 실패했습니다.", null);
        }
    }

    /**
     * 이메일 인증 확인
     */
    public DataResponse<String> verifyEmail(String email, String code) {
        boolean isVerified = emailUtil.verifyCode(email, code);

        if (isVerified) {
            return new DataResponse<>(200, "이메일 인증이 완료되었습니다.", null);
        } else {
            return new DataResponse<>(400, "잘못된 인증 코드이거나 만료되었습니다.", null);
        }
    }

    /**
     * 회원가입 처리
     */
    public DataResponse<String> register(LoginRequestDto request) {
        String email = request.getEmail();
        String password = request.getPassword();

        try {
            if (isEmailAlreadyExists(email)) {
                return new DataResponse<>(400, "이미 존재하는 이메일입니다", null);
            }

            String verificationCode = request.getVerificationCode();
            if (verificationCode == null || verificationCode.trim().isEmpty()) {
                return new DataResponse<>(400, "이메일 인증 코드가 필요합니다.", null);
            }

            if (!emailUtil.verifyCode(email, verificationCode)) {
                return new DataResponse<>(400, "잘못된 인증 코드이거나 만료되었습니다.", null);
            }

            if (!isValidPassword(password)) {
                return new DataResponse<>(400,
                        "비밀번호는 8-16자, 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다.", null);
            }

            User user = createNewUser(request);
            userRepository.save(user);

            emailUtil.removeVerifiedEmail(email);

            return new DataResponse<>(200, "회원가입 성공", "사용자가 생성되었습니다");

        } catch (Exception e) {
            log.error("회원가입 처리 중 오류: {}", e.getMessage());
            return new DataResponse<>(500, "회원가입 처리 중 오류가 발생했습니다.", null);
        }
    }

    private User createNewUser(LoginRequestDto request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setNickname(request.getNickname());


        if (request.getBirthDate() != null && !request.getBirthDate().isEmpty()) {
            try {
                LocalDate birthDate = LocalDate.parse(request.getBirthDate());
                user.setBirthday(birthDate.atStartOfDay());
            } catch (Exception e) {
                throw new RuntimeException("올바르지 않은 생년월일 형식입니다. (YYYY-MM-DD)");
            }
        }

        user.setCreateAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return user;
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

            User user = findUserByEmailSafely(email);

            if (user == null) {
                user = new User();
                user.setEmail(email);
                user.setName(name);
                user.setProfile(profile);
                user.setProvider(providerType);
                user.setPassword(passwordEncoder.encode("OAUTH_USER_" + System.currentTimeMillis()));
                user.setCreateAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                userRepository.save(user);
            }

            String jwtToken = jwtTokenProvider.createToken(user);
            String refreshToken = jwtTokenProvider.createRefreshToken(email);

            user.setRefreshToken(refreshToken);
            user.setRefreshTokenExpiry(LocalDateTime.now().plusDays(7));
            userRepository.save(user);

            LoginResponseDto responseDto = new LoginResponseDto(true, "OAuth 로그인 성공", jwtToken);
            return new DataResponse<>(200, "OAuth 로그인 성공", responseDto);

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("OAuth 로그인 실패: {}", e.getMessage());
            throw new CustomException(UserErrorCode.OAUTH_LOGIN_FAILED);
        }
    }

    public User getUserByEmail(String email) {
        User user = findUserByEmailSafely(email);
        if (user == null) {
            throw new CustomException(UserErrorCode.USER_NOT_FOUND);
        }
        return user;
    }

    public User getSafeUserInfo(String email) {
        User user = getUserByEmail(email);
        user.setPassword(null);
        user.setRefreshToken(null);
        return user;
    }

    private boolean isEmailAlreadyExists(String email) {
        try {
            User user = findUserByEmailSafely(email);
            return user != null;
        } catch (Exception e) {
            log.error("이메일 중복 확인 중 오류: {}", e.getMessage());
            return false;
        }
    }

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

    public DataResponse<String> sendPasswordResetEmail(String email) {
        try {
            log.info("비밀번호 재설정 이메일 발송 요청: {}", personalDataUtil.maskEmail(email));
            
            User user = findUserByEmailSafely(email);
            if (user == null) {
                return new DataResponse<>(404, "등록되지 않은 이메일입니다.", null);
            }

            boolean success = emailUtil.sendPasswordResetEmail(email);

            if (success) {
                return new DataResponse<>(200, "비밀번호 재설정 코드가 발송되었습니다.", null);
            } else {
                return new DataResponse<>(500, "이메일 발송에 실패했습니다.", null);
            }
        } catch (Exception e) {
            log.error("비밀번호 재설정 이메일 발송 실패: {}", e.getMessage(), e);
            return new DataResponse<>(500, "이메일 발송에 실패했습니다.", null);
        }
    }

    public DataResponse<String> resetPassword(String email, String newPassword, String verificationCode) {
        try {
            User user = findUserByEmailSafely(email);
            if (user == null) {
                return new DataResponse<>(404, "등록되지 않은 이메일입니다.", null);
            }

            if (!emailUtil.verifyCode(email, verificationCode)) {
                return new DataResponse<>(400, "잘못된 인증 코드이거나 만료되었습니다.", null);
            }

            if (!isValidPassword(newPassword)) {
                return new DataResponse<>(400,
                        "비밀번호는 8-16자, 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다.", null);
            }

            // 현재 비밀번호와 새 비밀번호가 같은지 확인
            if (passwordEncoder.matches(newPassword, user.getPassword())) {
                return new DataResponse<>(400, "새 비밀번호는 현재 비밀번호와 달라야 합니다.", null);
            }

            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            emailUtil.removeVerifiedEmail(email);

            log.info("비밀번호 재설정 성공: {}", personalDataUtil.maskEmail(email));
            return new DataResponse<>(200, "비밀번호가 성공적으로 변경되었습니다.", null);

        } catch (Exception e) {
            log.error("비밀번호 재설정 실패: {}", e.getMessage());
            return new DataResponse<>(500, "비밀번호 재설정 처리 중 오류가 발생했습니다.", null);
        }
    }

    @Transactional
    public DataResponse<String> logout(String email) {
        try {
            User user = findUserByEmailSafely(email);

            if (user == null) {
                return new DataResponse<>(404, "사용자를 찾을 수 없습니다.", null);
            }

            user.setRefreshToken(null);
            user.setRefreshTokenExpiry(null);
            userRepository.save(user);

            log.info("로그아웃 완료: {}", personalDataUtil.maskEmail(email));
            return new DataResponse<>(200, "로그아웃이 완료되었습니다.", null);

        } catch (Exception e) {
            log.error("로그아웃 실패: {}", e.getMessage());
            return new DataResponse<>(500, "로그아웃 처리 중 오류가 발생했습니다.", null);
        }
    }

    public DataResponse<String> sendDeleteAccountVerificationEmail(String email) {
        try {
            log.info("회원탈퇴 인증 이메일 발송 요청: {}", personalDataUtil.maskEmail(email));

            User user = findUserByEmailSafely(email);
            if (user == null) {
                return new DataResponse<>(404, "등록되지 않은 이메일입니다.", null);
            }

            boolean success = emailUtil.sendDeleteAccountEmail(email);

            if (success) {
                return new DataResponse<>(200, "회원탈퇴 인증 코드가 발송되었습니다.", null);
            } else {
                return new DataResponse<>(500, "이메일 발송에 실패했습니다.", null);
            }
        } catch (Exception e) {
            log.error("회원탈퇴 이메일 발송 실패: {}", e.getMessage(), e);
            return new DataResponse<>(500, "이메일 발송에 실패했습니다.", null);
        }
    }

    @Transactional
    public DataResponse<String> deleteUser(String email, String password, String verificationCode) {
        try {
            log.info("회원탈퇴 시도: {}", personalDataUtil.maskEmail(email));

            User user = findUserByEmailSafely(email);
            if (user == null) {
                return new DataResponse<>(404, "사용자를 찾을 수 없습니다.", null);
            }

            if (!passwordEncoder.matches(password, user.getPassword())) {
                return new DataResponse<>(400, "비밀번호가 일치하지 않습니다.", null);
            }

            if (!emailUtil.verifyCode(email, verificationCode)) {
                return new DataResponse<>(400, "잘못된 인증 코드이거나 만료되었습니다.", null);
            }

            userRepository.delete(user);
            emailUtil.removeVerifiedEmail(email);

            log.info("회원탈퇴 완료: {}", personalDataUtil.maskEmail(email));
            return new DataResponse<>(200, "회원탈퇴가 완료되었습니다.", null);

        } catch (Exception e) {
            log.error("회원탈퇴 실패: {}", e.getMessage(), e);
            return new DataResponse<>(500, "회원탈퇴 처리 중 오류가 발생했습니다.", null);
        }
    }
    @Transactional
    public void profileUpdate(MultipartFile profile, String nickname, String job) throws IOException {
        String imgUrl =imgUtil.imgUpload(profile);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userRepository.findByEmail(aesUtil.encode(email));
        if(profile != null) {
            user.get().addProfile(imgUrl);
        }else if(nickname != null) {
            user.get().setNickname(aesUtil.encode(nickname));
        }else if(job != null) {
            user.get().setJob(aesUtil.encode(job));
        }
    }

    public String getToken() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String encodeEmail = aesUtil.encode(email);
        Optional<User> user = userRepository.findByEmail(encodeEmail);

        return jwtTokenProvider.createToken(user.get());

    }

    public boolean getDuplicateNickname(String nickname) {
        String encodeNickname = aesUtil.encode(nickname);
        return userRepository.existsByNickname(encodeNickname);
    }

    @Transactional
    public DataResponse nicknameUpdate(String nickname) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if(email == null) {
            throw new CustomException(UserErrorCode.USER_NOT_FOUND);
        }else{
            User user = userRepository.findByEmail(aesUtil.encode(email)).get();
            user.setNickname(
                    aesUtil.encode(nickname)
            );
            DataResponse dataResponse = new DataResponse();
            dataResponse.setStatus(200);
            dataResponse.setMessage("닉네임이 성공적으로 변경했습니다.");
            return dataResponse;
        }
    }
    @Transactional
    public DataResponse jobUpdate(String job) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if(email == null) {
            throw new CustomException(UserErrorCode.USER_NOT_FOUND);
        }else{
            User user = userRepository.findByEmail(aesUtil.encode(email)).get();
            user.setJob(
                    aesUtil.encode(job)
            );
            DataResponse dataResponse = new DataResponse();
            dataResponse.setStatus(200);
            dataResponse.setMessage("job 변경되었습니다.");
            return dataResponse;
        }
    }
    @Transactional
    public DataResponse introductionUpdate(String introduction) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (email == null){
            throw new CustomException(UserErrorCode.USER_NOT_FOUND);
        }else{
            User user = userRepository.findByEmail(aesUtil.encode(email)).get();
            user.setIntroduce(
                    aesUtil.encode(introduction)
            );
            DataResponse dataResponse = new DataResponse();
            dataResponse.setStatus(200);
            dataResponse.setMessage("introduction 변경되었습니다.");
            return dataResponse;
        }
    }
}
