package com.cvsgo.exception;

import com.cvsgo.dto.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ErrorResponse handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        log.info("유효성 검사 실패 - {} '{}'", e.getFieldError().getDefaultMessage(), e.getFieldError().getRejectedValue());
        return ErrorResponse.of(e.getFieldError().getDefaultMessage(), "INVALID_REQUEST");
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(ExpiredJwtException.class)
    public ErrorResponse handleExpiredJwtException(ExpiredJwtException e) {
        return ErrorResponse.of(ErrorCode.EXPIRED_TOKEN.getCode(), ErrorCode.EXPIRED_TOKEN.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnexpectedException(Exception e) {
        log.error("Unexpected error occurred.", e);
        return ErrorResponse.of(e.getMessage(), "UNEXPECTED_ERROR");
    }

}
