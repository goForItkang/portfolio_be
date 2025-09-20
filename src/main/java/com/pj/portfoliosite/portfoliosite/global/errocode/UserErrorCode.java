package com.pj.portfoliosite.portfoliosite.global.errocode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND(404,"U001","사용자를 찾을 수 없습니다."),
    INVALID_PROVIDER(400,"U009","지원하지 않는 OAuth 제공자입니다."),
    OAUTH_LOGIN_FAILED(500,"U010","OAuth 로그인 중 오류가 발생했습니다."),
    OAUTH_TOKEN_ERROR(400,"U011","OAuth 토큰 처리 중 오류가 발생했습니다."),

    PASSWORD_RESET_INVALID_CODE(400, "U012", "잘못된 비밀번호 재설정 코드입니다."),
    PASSWORD_RESET_EXPIRED(400, "U013", "비밀번호 재설정 코드가 만료되었습니다."),
    PASSWORD_RESET_EMAIL_NOT_FOUND(404, "U014", "등록되지 않은 이메일입니다."),
    PASSWORD_INVALID_FORMAT(400, "U015", "비밀번호는 8-16자, 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다."),

    USER_DELETE_FAILED(500, "U016", "회원탈퇴 처리 중 오류가 발생했습니다."),
    USER_DELETE_PASSWORD_INCORRECT(400, "U017", "비밀번호가 일치하지 않습니다.");

    private final int status;
    private final String errorCode;
    private final String message;
}
