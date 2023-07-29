package com.cvsgo.dto.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginResponseDto {

    private Long userId;

    private String accessToken;

    private String refreshToken;

    private String tokenType;

    @Builder
    public LoginResponseDto(Long userId, String accessToken, String refreshToken,
        String tokenType) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
    }
}
