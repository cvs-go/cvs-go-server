package com.cvsgo.exception.user;

import com.cvsgo.exception.ErrorCode;

public class DuplicateNicknameException extends RuntimeException {

    private final ErrorCode errorCode;

    public DuplicateNicknameException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public String getCode() {
        return errorCode.getCode();
    }

}
