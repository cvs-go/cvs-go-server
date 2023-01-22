package com.cvsgo.exception.user;

import com.cvsgo.exception.ErrorCode;

public class DuplicateEmailException extends RuntimeException {

    private final ErrorCode errorCode;

    public DuplicateEmailException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public String getCode() {
        return errorCode.getCode();
    }

}
