package com.cvsgo.exception.product;

import com.cvsgo.exception.ErrorCode;

public class DuplicateProductBookmarkException extends RuntimeException {

    private final ErrorCode errorCode;

    public DuplicateProductBookmarkException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public String getCode() {
        return errorCode.getCode();
    }

}
