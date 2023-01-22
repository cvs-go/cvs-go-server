package com.cvsgo.dto;

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

}
