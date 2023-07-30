package com.cvsgo.dto.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginResponseDto {

    private Long userId;

    private String userNickname;

    private String accessToken;

    private String refreshToken;

    private String tokenType;

    @Builder
    public LoginResponseDto(Long userId, String userNickname, String accessToken, String refreshToken,
        String tokenType) {
        this.userId = userId;
        this.userNickname = userNickname;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
    }
}
