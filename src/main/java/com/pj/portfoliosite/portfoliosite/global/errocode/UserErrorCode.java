package com.pj.portfoliosite.portfoliosite.global.errocode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND(404,"U001","사용자를 찾을 수 없습니다."),
    INVALID_PROVIDER(400,"U009","지원하지 않는 OAuth 제공자입니다."),
    OAUTH_LOGIN_FAILED(500,"U010","OAuth 로그인 중 오류가 발생했습니다."),
    OAUTH_TOKEN_ERROR(400,"U011","OAuth 토큰 처리 중 오류가 발생했습니다.");

    private final int status;
    private final String errorCode;
    private final String message;
}
