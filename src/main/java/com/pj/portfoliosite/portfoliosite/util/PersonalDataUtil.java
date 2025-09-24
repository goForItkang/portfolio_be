package com.pj.portfoliosite.portfoliosite.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 개인정보 암호화/복호화 전용 유틸리티 클래스
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PersonalDataUtil {

    private final AESUtil aesUtil;

    /**
     * 개인정보 암호화가 필요한 필드들을 정의
     */
    private static final Set<String> ENCRYPTED_FIELDS = Set.of(
            "email", "name", "nickname", "job", "interest", "interest2",
            "gitLink", "introduce", "profile"
    );

    /**
     * 개인정보 암호화
     */
    public String encryptPersonalData(String data) {
        if (data == null || data.trim().isEmpty()) {
            return data;
        }
        
        // 이미 암호화된 데이터인지 확인
        if (aesUtil.isEncrypted(data)) {
            return data;
        }
        
        try {
            String encrypted = aesUtil.encode(data);
            return encrypted;
        } catch (Exception e) {
            log.error("개인정보 암호화 실패: {}", e.getMessage());
            return data; // 암호화 실패 시 원본 반환
        }
    }

    /**
     * 개인정보 복호화
     */
    public String decryptPersonalData(String encryptedData) {
        if (encryptedData == null || encryptedData.trim().isEmpty()) {
            return encryptedData;
        }
        
        // 짧은 데이터는 바로 반환
        if (encryptedData.length() < 4) {
            return encryptedData;
        }
        
        // 암호화되지 않은 데이터인지 확인
        if (!aesUtil.isEncrypted(encryptedData)) {
            return encryptedData;
        }
        
        try {
            String decrypted = aesUtil.decode(encryptedData);
            
            // 복호화 결과 검증
            if (decrypted != null && !decrypted.equals(encryptedData)) {
                return decrypted;
            } else {
                return encryptedData;
            }
        } catch (Exception e) {
            return encryptedData;
        }
    }
    
    /**
     * 이메일 마스킹 (로깅용)
     */
    public String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***";
        }
        
        String[] parts = email.split("@");
        if (parts.length != 2) {
            return "***";
        }
        
        String localPart = parts[0];
        String domain = parts[1];
        
        if (localPart.length() <= 2) {
            return localPart + "***@" + domain;
        } else {
            return localPart.substring(0, 2) + "***@" + domain;
        }
    }

    /**
     * 이미 암호화된 데이터인지 확인
     */
    public boolean isAlreadyEncrypted(String data) {
        return aesUtil.isEncrypted(data);
    }

    /**
     * 암호화 대상 필드인지 확인
     */
    public boolean isEncryptedField(String fieldName) {
        return ENCRYPTED_FIELDS.contains(fieldName);
    }
}
