package com.cvsgo.controller;

import com.cvsgo.dto.SuccessResponse;
import com.cvsgo.dto.auth.LoginRequestDto;
import com.cvsgo.dto.auth.TokenDto;
import com.cvsgo.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.cvsgo.util.AuthConstants.TOKEN_TYPE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public SuccessResponse<TokenDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        return SuccessResponse.of(authService.login(loginRequestDto));
    }

    @GetMapping("/tokens")
    public SuccessResponse<TokenDto> reissueToken(@RequestHeader("Authorization") String authorizationHeader) {
        String refreshToken = authService.extractToken(authorizationHeader, TOKEN_TYPE);
        return SuccessResponse.of(authService.reissueToken(refreshToken));
    }

}
