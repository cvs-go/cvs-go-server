package com.cvsgo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
public class SuccessResponse<T> extends Response {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T data;

    private SuccessResponse() {
        this.data = null;
    }

    private SuccessResponse(T data) {
        this.data = data;
    }

    public static SuccessResponse<Void> of() {
        return new SuccessResponse();
    }

    public static <T> SuccessResponse<T> of(T data) {
        return new SuccessResponse<>(data);
    }

}
