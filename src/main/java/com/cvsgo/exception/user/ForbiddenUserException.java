package com.cvsgo.exception.user;

import com.cvsgo.exception.ErrorCode;

public class ForbiddenUserException extends RuntimeException {

    private final ErrorCode errorCode;

    public ForbiddenUserException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public String getCode() {
        return errorCode.getCode();
    }

}
