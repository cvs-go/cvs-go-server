package com.cvsgo.dto;

import com.cvsgo.exception.ErrorCode;
import lombok.Getter;

@Getter
public class ErrorResponse extends Response {

    private final String message;
    private final String code;

    private ErrorResponse(String message, String code) {
        this.message = message;
        this.code = code;
    }

    public static ErrorResponse of(String message, String code) {
        return new ErrorResponse(message, code);
    }

    public static ErrorResponse from(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getMessage(), errorCode.getCode());
    }

}
