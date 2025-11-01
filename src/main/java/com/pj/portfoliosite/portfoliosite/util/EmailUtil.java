package com.pj.portfoliosite.portfoliosite.util;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
        
        // 수정: verified 초기값을 false로 변경
        VerificationInfo(String code, LocalDateTime expirationTime) {
            this.code = code;
            this.expirationTime = expirationTime;
            this.verified = true; // 수정됨: 초기에는 인증되지 않은 상태
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
            log.info("회원가입 인증 이메일 발송 성공: {}", maskEmail(email));
            return true;

        } catch (Exception e) {
            log.error("회원가입 인증 이메일 발송 실패: {}", e.getMessage());
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
            log.info("비밀번호 재설정 인증 이메일 발송 성공: {}", maskEmail(email));
            return true;
        } catch (Exception e) {
            log.error("비밀번호 재설정 인증 이메일 발송 실패: {}", e.getMessage());
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
            log.info("회원탈퇴 인증 이메일 발송 성공: {}", maskEmail(email));
            return true;
        } catch (Exception e) {
            log.error("회원탈퇴 인증 이메일 발송 실패: {}", e.getMessage());
            return false;
        }
    }

    // 인증 코드 확인 (강화된 디버깅 버전)
    public boolean verifyCode(String email, String code) {
        log.debug("=== 인증 코드 검증 시작 ===");
        log.debug("검증 대상 이메일: {}", maskEmail(email));
        log.debug("입력된 인증 코드: {}", code);
        
        VerificationInfo info = verificationMap.get(email);

        if (info == null) {
            log.warn("인증 코드 확인 실패 - 정보 없음: {}", maskEmail(email));
            log.debug("현재 저장된 인증 정보 목록:");
            for (String storedEmail : verificationMap.keySet()) {
                log.debug("- {}", maskEmail(storedEmail));
            }
            return false;
        }

        log.debug("저장된 인증 코드: {}", info.code);
        log.debug("코드 만료 시간: {}", info.expirationTime);
        log.debug("현재 시간: {}", LocalDateTime.now());
        
        if (info.isExpired()) {
            verificationMap.remove(email);
            log.warn("인증 코드 확인 실패 - 만료됨: {}", maskEmail(email));
            return false;
        }

        boolean codeMatches = info.code.equals(code);
        log.debug("코드 일치 여부: {} (저장됨: {}, 입력됨: {})", codeMatches, info.code, code);
        
        if (!codeMatches) {
            log.warn("인증 코드 확인 실패 - 코드 불일치: {} (저장됨: {}, 입력됨: {})", maskEmail(email), info.code, code);
            return false;
        }

        info.verified = true;
        log.info("인증 코드 확인 성공: {} - verified=true로 설정됨", maskEmail(email));
        return true;
    }

    // 이메일 인증 여부 확인 (강화된 디버깅 버전)
    public boolean isEmailVerified(String email) {
        log.debug("=== 이메일 인증 여부 확인 시작 ===");
        log.debug("확인 대상 이메일: {}", maskEmail(email));
        
        VerificationInfo info = verificationMap.get(email);
        
        if (info == null) {
            log.debug("인증 정보 없음 - {}: null", maskEmail(email));
            log.debug("현재 메모리에 저장된 전체 인증 요청 수: {}", verificationMap.size());
            
            // 디버깅: 메모리에 있는 모든 이메일 리스트 출력
            if (!verificationMap.isEmpty()) {
                log.debug("메모리에 저장된 이메일 목록:");
                for (String storedEmail : verificationMap.keySet()) {
                    VerificationInfo storedInfo = verificationMap.get(storedEmail);
                    log.debug("- {}: verified={}, expired={}", 
                        maskEmail(storedEmail), 
                        storedInfo.verified, 
                        storedInfo.isExpired());
                }
            }
            
            return false;
        }
        
        log.debug("인증 정보 발견 - {}", maskEmail(email));
        log.debug("인증 상태: verified={}", info.verified);
        log.debug("만료 여부: expired={}", info.isExpired());
        
        if (info.isExpired()) {
            log.debug("인증 정보 만료됨 - {}", maskEmail(email));
        }
        
        boolean isVerified = info.verified && !info.isExpired();
        log.debug("최종 인증 여부 결과 - {}: {}", maskEmail(email), isVerified);
        
        return isVerified;
    }

    public void cleanupExpiredCodes() {
        int beforeSize = verificationMap.size();
        verificationMap.entrySet().removeIf(entry -> entry.getValue().isExpired());
        int afterSize = verificationMap.size();
        log.debug("만료된 인증 코드 정리 완료: {} -> {}", beforeSize, afterSize);
    }

    public void removeVerifiedEmail(String email) {
        verificationMap.remove(email);
        log.debug("인증 정보 삭제: {}", maskEmail(email));
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

    // 디버깅용: 특정 이메일의 저장된 인증 코드 반환
    public String getStoredVerificationCode(String email) {
        VerificationInfo info = verificationMap.get(email);
        if (info == null) {
            return null;
        }
        return info.code;
    }

    // 이메일 마스킹 (로그용)
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***";
        }
        String[] parts = email.split("@");
        String localPart = parts[0];
        if (localPart.length() <= 2) {
            return "***@" + parts[1];
        }
        return localPart.substring(0, 2) + "***@" + parts[1];
    }

    // 실제 이메일 발송 처리
    private void sendEmailMessage(String email, String code, String purpose) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(email);

            if ("회원가입".equals(purpose)) {
                helper.setSubject("[Port Cloud] 이메일 인증 코드");
                helper.setText(createSignupEmailContent(code), true); // <-- HTML 모드 true
            } else if ("비밀번호재설정".equals(purpose)) {
                helper.setSubject("[Port Cloud] 비밀번호 재설정 인증 코드");
                helper.setText(createPasswordResetEmailContent(code), false); // 그냥 텍스트
            } else if ("회원탈퇴".equals(purpose)) {
                helper.setSubject("[Port Cloud] 회원탈퇴 인증 코드");
                helper.setText(createDeleteAccountEmailContent(code), false);
            }

            mailSender.send(mimeMessage);
            log.info("{} 이메일 발송 완료: {}", purpose, maskEmail(email));
        } catch (Exception e) {
            log.error("{} 이메일 발송 실패 - {}: {}", purpose, maskEmail(email), e.getMessage());
            e.printStackTrace();
        }
    }

    private String createSignupEmailContent(String code) {
        return """
        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; background: #f9f9f9;">
            <div style="background: #f3e8ff; border-radius: 12px; padding: 20px; text-align: center;">
                <h2 style="color: #5b21b6; margin-bottom: 10px;">PortCloud</h2>
                <p style="font-size: 16px; color: #333; margin-bottom: 20px;">
                    이메일 인증을 완료하려면 아래의 <b>인증번호</b>를 입력해주세요.
                </p>
                <div style="margin: 20px auto; padding: 20px; background: #fff; border-radius: 8px; width: fit-content;">
                    <h3 style="margin: 0; font-size: 14px; color: #555;">인증번호</h3>
                    <p style="font-size: 28px; font-weight: bold; margin: 10px 0; color: #111;">%s</p>
                    <small style="color: #777;">개인정보 보호를 위해<br>이메일 인증번호는 %d분간 유효합니다.</small>
                </div>
                <p style="font-size: 12px; color: #666; margin-top: 30px;">
                    본 메일은 관계 법령상 광고성 메일 수신 동의 여부와 무관하게 발송되었습니다.<br>
                    문의사항이 있으시면 PortCloud 고객센터로 연락 부탁드립니다.
                </p>
            </div>
        </div>
        """.formatted(code, expirationMinutes);
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

    /**
     * ===============================================
     * 비밀번호 재설정용 메서드 추가 (Step 3에서 사용)
     * ===============================================
     */

    /**
     * 저장된 인증 코드 삭제
     * 비밀번호 재설정 완료 후 호출하여 재사용 방지
     * 
     * @param email 사용자 이메일
     */
    public void clearStoredVerificationCode(String email) {
        if (verificationMap.remove(email) != null) {
            log.info("저장된 인증 코드 삭제 완료: {}", maskEmail(email));
        } else {
            log.warn("삭제할 인증 코드 없음: {}", maskEmail(email));
        }
    }
}
