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
    public ErrorResponse handleDuplicateEmailException(DuplicateEmailException exception) {
        return ErrorResponse.of(exception.getMessage(), exception.getCode());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateNicknameException.class)
    public ErrorResponse handleDuplicateEmailException(DuplicateNicknameException exception) {
        return ErrorResponse.of(exception.getMessage(), exception.getCode());
    }

}
