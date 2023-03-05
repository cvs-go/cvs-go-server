package com.cvsgo.exception.auth;

import com.cvsgo.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NotFoundUserException.class)
    public ErrorResponse handleUserNotFoundException(NotFoundUserException e) {
        return ErrorResponse.of(e.getMessage(), e.getCode());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidPasswordException.class)
    public ErrorResponse handleInvalidPasswordException(InvalidPasswordException e) {
        return ErrorResponse.of(e.getMessage(), e.getCode());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedUserException.class)
    public ErrorResponse handleUnauthorizedUserException(UnauthorizedUserException e) {
        return ErrorResponse.of(e.getMessage(), e.getCode());
    }

}
