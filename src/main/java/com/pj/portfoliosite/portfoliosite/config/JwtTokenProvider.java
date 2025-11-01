package com.pj.portfoliosite.portfoliosite.config;

import com.pj.portfoliosite.portfoliosite.global.entity.User;
import com.pj.portfoliosite.portfoliosite.util.AESUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret-key:eW91ci1zdXBlci1zZWNyZXQta2V5LWF0LWxlYXN0LTMyLWNoYXJhY3RlcnMtbG9uZw==}")
    private String secretKey;

    private final AESUtil aesUtil;

    // 기존 코드 (1시간, 7일)
    // private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1시간
    // private static final long REFRESH_TOKEN_EXPIRATION_TIME = 1000L * 60 * 60 * 24 * 7; // 7일

    // 테스트용 무제한
    private static final long EXPIRATION_TIME = 1000L * 60 * 60 * 24;
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 1000L * 60 * 60 * 24 * 365 * 100;

    private Key signingKey;

    @PostConstruct
    public void init() {
        if (secretKey == null || secretKey.trim().isEmpty()) {
            throw new IllegalArgumentException("JWT secret key는 필수입니다.");
        }

        try {
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            throw new IllegalArgumentException("올바르지 않은 JWT secret key 형식입니다.", e);
        }
    }

    private Key getSigningKey() {
        if (signingKey == null) {
            throw new IllegalStateException("JWT signing key가 초기화되지 않았습니다.");
        }
        return signingKey;
    }

    public String createToken(User user) {
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("이메일은 null이거나 빈 값일 수 없습니다.");
        }

        Date now = new Date();
        Date expiry = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("nickname",
                        aesUtil.decode(user.getNickname()))
                .claim("name",
                        aesUtil.decode(user.getName()))
                .claim("profileUrl",aesUtil.decode(user.getProfile()))
                .claim("job",aesUtil.decode(user.getJob()))
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("이메일은 null이거나 빈 값일 수 없습니다.");
        }

        Date now = new Date();
        Date expiry = new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * ===============================================
     * 비밀번호 재설정용 토큰 메서드
     * ===============================================
     */

    /**
     * 비밀번호 재설정용 토큰 생성
     * 
     * @param email 사용자 이메일
     * @param expirationSeconds 토큰 유효 기간 (초 단위)
     * @return 리셋 토큰
     */
    public String createResetToken(String email, long expirationSeconds) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("이메일은 null이거나 빈 값일 수 없습니다.");
        }

        Date now = new Date();
        Date expiry = new Date(now.getTime() + (expirationSeconds * 1000)); // 초를 밀리초로 변환

        return Jwts.builder()
                .setSubject(email)
                .claim("type", "reset") // 토큰 타입 구분
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 비밀번호 재설정 토큰 검증 및 이메일 추출
     * 
     * @param token 리셋 토큰
     * @return 이메일 (유효한 경우) 또는 null (만료/유효하지 않은 경우)
     */
    public String validateResetToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // 토큰 타입 검증 (reset 토큰인지 확인)
            String type = claims.get("type", String.class);
            if (!"reset".equals(type)) {
                return null;
            }

            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            // 토큰이 만료된 경우
            return null;
        } catch (JwtException | IllegalArgumentException e) {
            // 토큰이 유효하지 않은 경우
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}