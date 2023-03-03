package com.cvsgo.exception.auth;

import com.cvsgo.exception.ErrorCode;

public class UnauthorizedUserException extends RuntimeException {

    private final ErrorCode errorCode;

    public UnauthorizedUserException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public String getCode() {
        return errorCode.getCode();
    }
}
