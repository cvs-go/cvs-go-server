package com.cvsgo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
public class SuccessResponse<T> extends Response {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T data;

    private SuccessResponse(T data) {
        this.data = data;
    }

    public static <T> SuccessResponse<T> create() {
        return new SuccessResponse<>(null);
    }

    public static <T> SuccessResponse<T> from(T data) {
        return new SuccessResponse<>(data);
    }

}
