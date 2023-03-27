package com.cvsgo.exception.product;

import com.cvsgo.exception.ErrorCode;

public class NotFoundProductBookmarkException extends RuntimeException {

    private final ErrorCode errorCode;

    public NotFoundProductBookmarkException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public String getCode() {
        return errorCode.getCode();
    }

}
