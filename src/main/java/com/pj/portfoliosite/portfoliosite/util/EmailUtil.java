package com.pj.portfoliosite.portfoliosite.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailUtil {

    private final JavaMailSender mailSender;

    @Value("${email.verification.from:jinusong14@gmail.com}")
    private String fromEmail;

    @Value("${email.verification.expiration-minutes:30}")
    private int expirationMinutes;

    private final Map<String, VerificationInfo> verificationMap = new ConcurrentHashMap<>();

    private static class VerificationInfo {
        final String code;
        final LocalDateTime expirationTime;
        boolean verified;

        VerificationInfo(String code, LocalDateTime expirationTime) {
            this.code = code;
            this.expirationTime = expirationTime;
            this.verified = false;
        }

        boolean isExpired() {
            return LocalDateTime.now().isAfter(expirationTime);
        }
    }

    // 6자리 인증 코드 생성
    public String generateVerificationCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    public boolean sendVerificationEmail(String email) {
        try {
            verificationMap.remove(email);

            String verificationCode = generateVerificationCode();
            LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(expirationMinutes);

            verificationMap.put(email, new VerificationInfo(verificationCode, expirationTime));

            sendEmailMessage(email, verificationCode, "회원가입");

            return true;

        } catch (Exception e) {
            return false;
        }
    }


    // 비밀번호 재설정용 이메일 발송
    public boolean sendPasswordResetEmail(String email) {
        try {
            verificationMap.remove(email);
            String verificationCode = generateVerificationCode();
            LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(expirationMinutes);
            verificationMap.put(email, new VerificationInfo(verificationCode, expirationTime));

            sendEmailMessage(email, verificationCode, "비밀번호재설정");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 회원탈퇴용 이메일 발송
    public boolean sendDeleteAccountEmail(String email) {
        try {
            verificationMap.remove(email);
            String verificationCode = generateVerificationCode();
            LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(expirationMinutes);
            verificationMap.put(email, new VerificationInfo(verificationCode, expirationTime));

            sendEmailMessage(email, verificationCode, "회원탈퇴");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 인증 코드 확인
    public boolean verifyCode(String email, String code) {
        VerificationInfo info = verificationMap.get(email);

        if (info == null) {
            return false;
        }

        if (info.isExpired()) {
            verificationMap.remove(email);
            return false;
        }

        if (!info.code.equals(code)) {
            return false;
        }

        info.verified = true;
        return true;
    }

    // 이메일 인증 여부 확인
    public boolean isEmailVerified(String email) {
        VerificationInfo info = verificationMap.get(email);
        return info != null && info.verified && !info.isExpired();
    }

    public void cleanupExpiredCodes() {
        verificationMap.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    public void removeVerifiedEmail(String email) {
        verificationMap.remove(email);
    }

    // 현재 메모리에 저장된 인증 요청 개수 반환
    public int getVerificationCount() {
        return verificationMap.size();
    }

    // 특정 이메일의 남은 만료 시간 반환
    public long getRemainingMinutes(String email) {
        VerificationInfo info = verificationMap.get(email);
        if (info == null || info.isExpired()) {
            return -1;
        }
        return java.time.Duration.between(LocalDateTime.now(), info.expirationTime).toMinutes();
    }


    // 실제 이메일 발송 처리
    private void sendEmailMessage(String email, String code, String purpose) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(email);

        if ("회원가입".equals(purpose)) {
            message.setSubject("[Port Cloud] 이메일 인증 코드");
            message.setText(createSignupEmailContent(code));
        } else if ("비밀번호재설정".equals(purpose)) {
            message.setSubject("[Port Cloud] 비밀번호 재설정 인증 코드");
            message.setText(createPasswordResetEmailContent(code));
        } else if ("회원탈퇴".equals(purpose)) {
            message.setSubject("[Port Cloud] 회원탈퇴 인증 코드");
            message.setText(createDeleteAccountEmailContent(code));
        }

        mailSender.send(message);
    }

     private String createSignupEmailContent(String code) {
        return "안녕하세요!\n\n" +
                "Port Cloud 회원가입을 위한 이메일 인증 코드입니다.\n\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                "   인증 코드: " + code + "\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                "• 이 코드는 " + expirationMinutes + "분간 유효합니다.\n" +
                "• 코드를 타인과 공유하지 마세요.\n" +
                "감사합니다.\n" +
                "Port Cloud";
     }

    private String createPasswordResetEmailContent(String code) {
        return "안녕하세요!\n\n" +
                "Port Cloud 비밀번호 재설정을 위한 인증 코드입니다.\n\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                "   인증 코드: " + code + "\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                "• 이 코드는 " + expirationMinutes + "분간 유효합니다.\n" +
                "• 코드를 타인과 공유하지 마세요.\n" +
                "감사합니다.\n" +
                "Port Cloud";
    }
    
    private String createDeleteAccountEmailContent(String code) {
        return "안녕하세요!\n\n" +
                "Port Cloud 회원탈퇴를 위한 인증 코드입니다.\n\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                "   인증 코드: " + code + "\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                "• 이 코드는 " + expirationMinutes + "분간 유효합니다.\n" +
                "• 코드를 타인과 공유하지 마세요.\n" +
                "• 회원탈퇴 시 모든 데이터가 영구적으로 삭제됩니다.\n" +
                "감사합니다.\n" +
                "Port Cloud";
    }
}