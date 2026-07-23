package com.han.community.global.exception;

import com.han.community.dto.common.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException e, HttpServletRequest req) {

        ErrorCode code = e.getErrorCode();
        log.warn("Business exception: {} - {}", code.name(), e.getMessage());

        return ResponseEntity
                .status(code.getStatus())
                .body(new ErrorResponse(new ErrorResponse.Error(
                        code.name(),
                        e.getMessage(),
                        null,
                        MDC.get("requestId"),
                        Instant.now()
                )));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handlerUnexpected(Exception e) {

        if(e instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }

        log.error("Unexpected exception", e);
        // todo: 서버에러발생 알림 기능 추가

        return ResponseEntity
                .internalServerError()
                .body(new ErrorResponse(new ErrorResponse.Error(
                        ErrorCode.INTERNAL_ERROR.name(),
                        ErrorCode.INTERNAL_ERROR.getMessage(),
                        null,
                        MDC.get("requestId"),
                        Instant.now()
                )));
    }
}
