package com.cvsgo.exception.user;

import com.cvsgo.exception.ErrorCode;

public class NotFoundUserFollowException extends RuntimeException {

    private final ErrorCode errorCode;

    public NotFoundUserFollowException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public String getCode() {
        return errorCode.getCode();
    }

}
