package com.pj.portfoliosite.portfoliosite.config;

import com.pj.portfoliosite.portfoliosite.global.entity.User;
import com.pj.portfoliosite.portfoliosite.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = resolveToken(request);

            if (token != null && jwtTokenProvider.validateToken(token)) {
                String email = jwtTokenProvider.getEmailFromToken(token);

                if (email != null) {
                    Optional<User> userOpt = userRepository.findByEmail(email);

                    if (userOpt.isPresent()) {
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        email,
                                        null,
                                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                                );

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
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
        
        // 인증이 필요한 경로들을 명시적으로 제외
        if (path.matches("/api/teampost/\\d+/like") ||
            path.matches("/api/teampost/\\d+/bookmark") ||
            path.matches("/api/teampost/\\d+/publish") ||
            path.matches("/api/teampost/\\d+/draft") ||
            path.startsWith("/api/user/teamposts/drafts") ||
            (path.startsWith("/api/teampost") && !path.equals("/api/teampost") && !path.startsWith("/api/teamposts") && !"GET".equals(method))) {
            return false;
        }
        
        return path.startsWith("/api/user/oauth/") ||
                path.startsWith("/api/user/login") ||
                path.startsWith("/api/user/register") ||
                path.startsWith("/api/user/send-verification") ||
                path.startsWith("/api/user/verify-email") ||
                path.startsWith("/api/teamposts") ||  // GET 목록 조회만 허용
                (path.matches("/api/teampost/\\d+") && "GET".equals(method)) ||  // GET 상세 조회만 허용
                path.startsWith("/swagger-ui") ||
                path.startsWith("/api-docs") ||
                path.equals("/test") ||
                path.equals("/test2");
    }
}