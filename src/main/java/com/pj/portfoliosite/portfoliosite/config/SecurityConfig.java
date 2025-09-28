package com.pj.portfoliosite.portfoliosite.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pj.portfoliosite.portfoliosite.global.handler.CustomAccessDeneHandler;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAccessDeneHandler accessDeneHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/",
                                "/test",
                                "/test2",
                                "/api/user/login",
                                "/api/user/register",
                                "/api/user/send-verification",
                                "/api/user/send-verification-email",
                                "/api/user/verify-email",
                                "/api/user/password-reset-request",
                                "/api/user/password-reset",
                                "/api/user/oauth/*/url",
                                "/api/user/oauth/*/callback",
                                "/api/teamposts",
                                "/api/teampost/**",
                                "/api/admin/migration/**",
                                "/api/portfolio/recommend",
                                "/api/projects/recommend",
                                "/api/skills",
                                "/api/portfolios/all",
                                "api/portfolio"
                        ).permitAll()
                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET,
                                "/api/teamposts",
                                "/api/teampost/*",
                                "/api/teampost/*/details"
                        ).permitAll()
                        .requestMatchers(
                                "/api/teampost/**"
                        ).hasRole("USER")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(accessDeneHandler)
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json;charset=UTF-8");

                            Map<String, Object> errorResponse = new HashMap<>();
                            errorResponse.put("status", 401);
                            errorResponse.put("error", "UNAUTHORIZED");
                            errorResponse.put("message", "인증이 필요합니다.");
                            errorResponse.put("path", request.getRequestURI());

                            ObjectMapper objectMapper = new ObjectMapper();
                            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
                        })
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

}