package com.cvsgo.exception;

import com.cvsgo.dto.ErrorResponse;
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
    protected ErrorResponse handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        log.info("유효성 검사 실패 - {} '{}'", ex.getFieldError().getDefaultMessage(), ex.getFieldError().getRejectedValue());
        return ErrorResponse.of(ex.getFieldError().getDefaultMessage(), "INVALID_REQUEST");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnexpectedException(Exception e) {
        log.error("Unexpected error occurred.", e);
        return ErrorResponse.of(e.getMessage(), "UNEXPECTED_ERROR");
    }

}
