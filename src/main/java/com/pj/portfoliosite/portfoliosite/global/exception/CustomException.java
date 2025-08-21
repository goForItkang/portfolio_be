package com.pj.portfoliosite.portfoliosite.global.exception;

import com.pj.portfoliosite.portfoliosite.global.errocode.ErrorCode;

public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
