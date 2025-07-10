package com.pj.portfoliosite.portfoliosite.util;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
@Component
public class AESUtil {

    @Value("${security.aes.secret-key}")
    private String aesSecretKey;

    private SecretKeySpec secretKeySpec;

    @PostConstruct
    public void init() {
        byte[] keyBytes = aesSecretKey.getBytes(StandardCharsets.UTF_8);
        secretKeySpec = new SecretKeySpec(keyBytes, "AES");
    }

    public String generateIV() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return Base64.getEncoder().encodeToString(iv);
    }

    public String encode(String plainText) {
        try {
            String iv = generateIV();
            IvParameterSpec ivSpec = new IvParameterSpec(Base64.getDecoder().decode(iv));

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            return iv + Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            log.error("Encoding error", e);
            throw new RuntimeException("Encoding error");
        }
    }

    public String decode(String encodedText) {
        try {
            String ivBase64 = encodedText.substring(0, 24);
            String cipherBase64 = encodedText.substring(24);

            IvParameterSpec ivSpec = new IvParameterSpec(Base64.getDecoder().decode(ivBase64));
            byte[] encrypted = Base64.getDecoder().decode(cipherBase64);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);
            byte[] decrypted = cipher.doFinal(encrypted);

            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Decoding error", e);
            throw new RuntimeException("Decoding error");
        }
    }
}
