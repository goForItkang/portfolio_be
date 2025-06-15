package com.pj.portfoliosite.portfoliosite.global.errocode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND(404,"U001","사용자를 찾을 수 없습니다.");

    private final int status;
    private final String errorCode;
    private final String message;
}
