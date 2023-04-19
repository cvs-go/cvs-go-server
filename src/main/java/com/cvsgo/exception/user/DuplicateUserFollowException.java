package com.cvsgo.exception.user;

import com.cvsgo.exception.ErrorCode;

public class DuplicateUserFollowException extends RuntimeException {

    private final ErrorCode errorCode;

    public DuplicateUserFollowException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public String getCode() {
        return errorCode.getCode();
    }

}
