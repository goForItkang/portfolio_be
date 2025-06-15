package com.pj.portfoliosite.portfoliosite.global.errocode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private final int status;
    private final String errorCode;
    private final String message;
}
