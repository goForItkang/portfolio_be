package com.pj.portfoliosite.portfoliosite.global.exception;

import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import com.pj.portfoliosite.portfoliosite.global.errocode.ErrorCode;
import com.pj.portfoliosite.portfoliosite.global.errocode.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        return ResponseEntity.status(errorCode.getStatus())
                .body(
                        new ErrorResponse(errorCode.getStatus(), errorCode.getErrorCode(),errorCode.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<DataResponse> handleRuntimeException(RuntimeException ex) {
        log.error("RuntimeException 발생: {}", ex.getMessage(), ex);
        
        DataResponse response = new DataResponse();
        response.setStatus(400);
        response.setMessage(ex.getMessage());
        response.setData(null);
        
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<DataResponse> handleAuthenticationException(AuthenticationException ex) {
        log.error("AuthenticationException 발생: {}", ex.getMessage());
        
        DataResponse response = new DataResponse();
        response.setStatus(401);
        response.setMessage("인증이 필요합니다.");
        response.setData(null);
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<DataResponse> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("AccessDeniedException 발생: {}", ex.getMessage());
        
        DataResponse response = new DataResponse();
        response.setStatus(403);
        response.setMessage("접근이 거부되었습니다.");
        response.setData(null);
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DataResponse> handleGeneralException(Exception ex) {
        log.error("예상치 못한 오류 발생: {}", ex.getMessage(), ex);
        
        DataResponse response = new DataResponse();
        response.setStatus(500);
        response.setMessage("서버 내부 오류가 발생했습니다.");
        response.setData(null);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

}
