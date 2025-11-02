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
}
