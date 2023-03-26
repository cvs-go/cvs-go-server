package com.cvsgo.exception.product;

import com.cvsgo.exception.ErrorCode;

public class NotFoundProductLikeException extends RuntimeException {

    private final ErrorCode errorCode;

    public NotFoundProductLikeException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public String getCode() {
        return errorCode.getCode();
    }

}
