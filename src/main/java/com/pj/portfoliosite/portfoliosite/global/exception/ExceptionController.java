package com.pj.portfoliosite.portfoliosite.global.exception;

import com.pj.portfoliosite.portfoliosite.global.errocode.ErrorCode;
import com.pj.portfoliosite.portfoliosite.global.errocode.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        return ResponseEntity.status(errorCode.getStatus())
                .body(
                        new ErrorResponse(errorCode.getStatus(), errorCode.getErrorCode(),errorCode.getMessage()));
    }

}
