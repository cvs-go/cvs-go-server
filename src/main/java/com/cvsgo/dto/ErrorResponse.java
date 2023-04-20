package com.cvsgo.dto;

import com.cvsgo.exception.ErrorCode;
import lombok.Getter;

@Getter
public class ErrorResponse extends Response {

    private final String code;
    private final String message;

    private ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ErrorResponse from(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.name(), errorCode.getMessage());
    }

    public static ErrorResponse of(String code, String message) {
        return new ErrorResponse(code, message);
    }

}
