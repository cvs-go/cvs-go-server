package com.cvsgo.exception.auth;

import com.cvsgo.exception.ErrorCode;

public class InvalidPasswordException extends RuntimeException {

    private final ErrorCode errorCode;
    public InvalidPasswordException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public String getCode() {
        return errorCode.getCode();
    }
}
