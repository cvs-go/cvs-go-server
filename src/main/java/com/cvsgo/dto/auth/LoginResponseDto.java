package com.cvsgo.dto.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginResponseDto {

    private Long userId;

    private TokenDto token;

    @Builder
    public LoginResponseDto(Long userId, TokenDto token) {
        this.userId = userId;
        this.token = token;
    }
}
