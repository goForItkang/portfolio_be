package com.pj.portfoliosite.portfoliosite.config;

import com.pj.portfoliosite.portfoliosite.global.entity.User;
import com.pj.portfoliosite.portfoliosite.user.UserRepository;
import com.pj.portfoliosite.portfoliosite.util.PersonalDataUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PersonalDataUtil personalDataUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = resolveToken(request);
            String requestURI = request.getRequestURI();
            
            log.debug("JWT 필터 처리 시작 - URI: {}, Token: {}", requestURI, token != null ? "[있음]" : "[없음]");


            if (token != null && jwtTokenProvider.validateToken(token)) {
                String email = jwtTokenProvider.getEmailFromToken(token);
                log.debug("토큰에서 추출한 이메일: {}", personalDataUtil.maskEmail(email));

                if (email != null) {
                    User user = findUserByEmailSafely(email);

                    if (user != null) {
                        log.debug("사용자 인증 성공 - ID: {}", user.getId());
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        email,
                                        null,
                                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                                );

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else {
                        log.warn("토큰에서 추출한 이메일에 해당하는 사용자 없음: {}", personalDataUtil.maskEmail(email));
                    }
                } else {
                    log.warn("토큰에서 이메일 추출 실패");
                }
            } else {
                if (token != null) {
                    log.warn("유효하지 않은 JWT 토큰");
                }
            }
        } catch (Exception e) {
            log.error("JWT 필터 처리 중 오류: {}", e.getMessage(), e);
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
    
    /**
     * 안전한 사용자 검색 (평문/암호화 모두 지원) - 개선된 버전
     */
    private User findUserByEmailSafely(String email) {
        try {
            // 1단계: 평문 이메일로 직접 검색 (가장 빠른 방법)
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                log.debug("평문 이메일로 사용자 발견");
                return userOpt.get();
            }

            // 2단계: 암호화된 이메일로 검색
            try {
                String encryptedEmail = personalDataUtil.encryptPersonalData(email);
                userOpt = userRepository.findByEmail(encryptedEmail);
                if (userOpt.isPresent()) {
                    log.debug("암호화된 이메일로 사용자 발견");
                    return userOpt.get();
                }
            } catch (Exception e) {
                log.debug("암호화 검색 실패, 계속 진행: {}", e.getMessage());
            }

            // 3단계: 전체 사용자 대상 복호화 비교 (최후의 수단)
            List<User> allUsers = userRepository.findAllForMigration();
            for (User user : allUsers) {
                try {
                    String userEmail = user.getEmail();
                    if (userEmail == null) continue;
                    
                    // 평문 비교
                    if (email.equalsIgnoreCase(userEmail)) {
                        log.debug("전체 검색 - 평문 비교로 사용자 발견");
                        return user;
                    }
                    
                    // 암호화된 데이터로 보이는 경우만 복호화 시도
                    if (userEmail.length() > 24 && !userEmail.contains("@")) {
                        try {
                            String decryptedEmail = personalDataUtil.decryptPersonalData(userEmail);
                            if (email.equalsIgnoreCase(decryptedEmail)) {
                                log.debug("전체 검색 - 복호화 비교로 사용자 발견");
                                return user;
                            }
                        } catch (Exception decryptError) {
                            // 복호화 실패는 조용히 무시 (다른 키로 암호화된 데이터일 수 있음)
                        }
                    }
                } catch (Exception e) {
                    // 개별 사용자 처리 실패 무시
                }
            }
            
            return null;
            
        } catch (Exception e) {
            log.error("사용자 검색 중 치명적 오류: {}", e.getMessage());
            return null;
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        log.debug("shouldNotFilter 검사 - URI: {}, Method: {}", path, method);

        // 인증이 필요하지 않은 경로들 (permitAll)
        boolean shouldSkip = path.startsWith("/api/user/oauth/") ||
                path.equals("/api/user/login") ||
                path.equals("/api/user/register") ||
                path.equals("/api/user/send-verification") ||
                path.equals("/api/user/send-verification-email") || // 추가 경로
                path.equals("/api/user/verify-email") ||
                path.equals("/api/user/password-reset-request") ||
                path.equals("/api/user/password-reset") ||
                path.startsWith("/api/teamposts") ||
                (path.matches("/api/teampost/\\d+/details") && "GET".equals(method)) ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/api-docs") ||
                path.startsWith("/api/admin/migration") ||
                path.equals("/test") ||
                path.equals("/test2") ||
                path.equals("/api");
        
        log.debug("shouldNotFilter 결과 - URI: {}, Skip: {}", path, shouldSkip);
        
        return shouldSkip;
    }
}
