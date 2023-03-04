package com.cvsgo.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LogoutRequestDto {

    private String token;

    @Builder
    public LogoutRequestDto(String token) {
        this.token = token;
    }
}
