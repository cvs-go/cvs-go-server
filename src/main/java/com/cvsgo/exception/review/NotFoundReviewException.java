package com.cvsgo.exception.review;

import com.cvsgo.exception.ErrorCode;

public class NotFoundReviewException extends RuntimeException {

    private final ErrorCode errorCode;

    public NotFoundReviewException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public String getCode() {
        return errorCode.getCode();
    }
}
