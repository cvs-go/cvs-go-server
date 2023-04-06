package com.cvsgo.exception.user;

import com.cvsgo.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserExceptionHandler {

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateEmailException.class)
    public ErrorResponse handleDuplicateEmailException(DuplicateEmailException e) {
        return ErrorResponse.of(e.getMessage(), e.getCode());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateNicknameException.class)
    public ErrorResponse handleDuplicateNicknameException(DuplicateNicknameException e) {
        return ErrorResponse.of(e.getMessage(), e.getCode());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ForbiddenUserException.class)
    public ErrorResponse handleForbiddentUserException(ForbiddenUserException e) {
        return ErrorResponse.of(e.getMessage(), e.getCode());
    }

}
