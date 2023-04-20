package com.cvsgo.exception;

import lombok.Getter;

@Getter
public class UnexpectedException extends RuntimeException {

    private final ErrorCode errorCode;

    public UnexpectedException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
