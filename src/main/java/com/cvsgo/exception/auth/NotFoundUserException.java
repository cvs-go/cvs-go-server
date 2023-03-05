package com.cvsgo.exception.auth;

import com.cvsgo.exception.ErrorCode;

public class NotFoundUserException extends RuntimeException {

    private final ErrorCode errorCode;

    public NotFoundUserException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public String getCode() {
        return errorCode.getCode();
    }

}
