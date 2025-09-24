package com.pj.portfoliosite.portfoliosite.util;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Component
public class AESUtil {

    @Value("${security.aes.secret-key:defaultKey1234567890abcdef}")
    private String aesSecretKey;

    private SecretKeySpec secretKeySpec;

    @PostConstruct
    public void init() {
        try {
            byte[] keyBytes = aesSecretKey.getBytes(StandardCharsets.UTF_8);
            
            // 키 길이를 16바이트(128비트)로 정규화
            byte[] normalizedKey = new byte[16];
            if (keyBytes.length >= 16) {
                System.arraycopy(keyBytes, 0, normalizedKey, 0, 16);
            } else {
                System.arraycopy(keyBytes, 0, normalizedKey, 0, keyBytes.length);
                // 부족한 부분은 0으로 패딩
                for (int i = keyBytes.length; i < 16; i++) {
                    normalizedKey[i] = 0;
                }
            }
            
            secretKeySpec = new SecretKeySpec(normalizedKey, "AES");
            log.info("AES 암호화 키 초기화 완료");
            
        } catch (Exception e) {
            log.error("AES 키 초기화 실패: {}", e.getMessage());
            throw new RuntimeException("AES 키 초기화에 실패했습니다.", e);
        }
    }

    /**
     * 암호화
     */
    public String encode(String plainText) {
        try {
            if (plainText == null || plainText.trim().isEmpty()) {
                return plainText;
            }

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            log.error("암호화 실패: {}", e.getMessage());
            return plainText; // 암호화 실패 시 원본 반환
        }
    }

    /**
     * 복호화
     */
    public String decode(String encodedText) {
        if (encodedText == null || encodedText.trim().isEmpty()) {
            return encodedText;
        }

        // 길이 검증 (매우 짧은 데이터는 평문으로 간주)
        if (encodedText.length() < 4) {
            return encodedText;
        }

        // 이메일 패턴 확인
        if (isPlainTextEmail(encodedText)) {
            return encodedText;
        }

        // URL 패턴 확인
        if (encodedText.startsWith("http://") || encodedText.startsWith("https://")) {
            return encodedText;
        }

        // Base64 검증 및 복호화
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(encodedText);
            
            // AES 블록 크기(16바이트) 배수 확인
            if (decodedBytes.length == 0 || decodedBytes.length % 16 != 0) {
                return encodedText;
            }
            
            // 실제 복호화 시도
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] decrypted = cipher.doFinal(decodedBytes);
            
            String result = new String(decrypted, StandardCharsets.UTF_8);
            
            // 결과 검증
            if (isValidDecryptedData(result)) {
                return result;
            } else {
                return encodedText;
            }
            
        } catch (Exception e) {
            // 복호화 실패 시 원본 반환
            return encodedText;
        }
    }

    /**
     * 평문 이메일인지 확인
     */
    private boolean isPlainTextEmail(String text) {
        if (text == null || text.length() < 5 || text.length() > 100) {
            return false;
        }
        return text.contains("@") && 
               text.contains(".") &&
               text.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }

    /**
     * 복호화된 데이터가 유효한지 검증
     */
    private boolean isValidDecryptedData(String data) {
        if (data == null || data.trim().isEmpty()) {
            return false;
        }
        
        // 제어 문자 확인 (일반적인 텍스트가 아님)
        for (char c : data.toCharArray()) {
            if (Character.isISOControl(c) && c != '\n' && c != '\r' && c != '\t') {
                return false;
            }
        }
        
        // 너무 많은 비ASCII 문자가 있으면 복호화 실패로 간주
        long nonAsciiCount = data.chars().filter(c -> c > 127).count();
        if (nonAsciiCount > data.length() / 2) {
            return false;
        }
        
        return true;
    }

    /**
     * 데이터가 암호화되어 있는지 확인
     */
    public boolean isEncrypted(String data) {
        if (data == null || data.trim().isEmpty()) {
            return false;
        }
        
        // 길이가 너무 짧으면 암호화된 것이 아님
        if (data.length() < 4) {
            return false;
        }
        
        // 평문 이메일이면 암호화되지 않음
        if (isPlainTextEmail(data)) {
            return false;
        }
        
        // URL이면 암호화되지 않음
        if (data.startsWith("http://") || data.startsWith("https://")) {
            return false;
        }
        
        try {
            byte[] decoded = Base64.getDecoder().decode(data);
            // AES 블록 크기 확인
            return decoded.length > 0 && decoded.length % 16 == 0;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
