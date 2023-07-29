package com.cvsgo.dto.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReissueTokenResponseDto {

    private String accessToken;

    private String refreshToken;

    private String tokenType;

    @Builder
    public ReissueTokenResponseDto(String accessToken, String refreshToken, String tokenType) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
    }
}
