package com.cvsgo.service;

import com.cvsgo.dto.auth.LoginRequestDto;
import com.cvsgo.dto.auth.TokenDto;
import com.cvsgo.entity.User;
import com.cvsgo.exception.auth.InvalidPasswordException;
import com.cvsgo.exception.auth.NotFoundUserException;
import com.cvsgo.repository.RefreshTokenRepository;
import com.cvsgo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = AuthService.class, initializers = ConfigDataApplicationContextInitializer.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Spy
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private AuthService authService;

    @BeforeEach
    public void setup() {
        final String secretKey = "ThisIsTestSecretKeyThisIsTestSecretKeyThisIsTestSecretKey";
        authService = new AuthService(secretKey, userRepository, passwordEncoder, refreshTokenRepository);

    }

    @Test
    @DisplayName("로그인에 성공하면 토큰을 얻는다")
    void should_get_tokens_when_succeed_to_login() {
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email("abc@naver.com")
                .password("password1!")
                .build();
        User user = User.builder()
                .userId("abc@naver.com")
                .password(passwordEncoder.encode("password1!"))
                .build();
        given(userRepository.findByUserId(loginRequestDto.getEmail()))
                .willReturn(Optional.of(user));

        TokenDto tokenDto = authService.login(loginRequestDto);

        assertNotNull(tokenDto.getAccessToken());
        assertNotNull(tokenDto.getRefreshToken());

    }

    @Test
    @DisplayName("존재하지 않는 사용자이면 NotFoundUserException이 발생한다")
    void should_throw_NotFoundUserException_when_user_does_not_exist() {
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email("abc@naver.com")
                .password("password1!")
                .build();

        given(userRepository.findByUserId(loginRequestDto.getEmail()))
                .willReturn(Optional.empty());

        assertThrows(NotFoundUserException.class, () -> authService.login(loginRequestDto));

        then(userRepository).should(times(1)).findByUserId(any());
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 InvalidPasswordException이 발생한다")
    void should_throw_InvalidPasswordException_when_password_is_not_correct() {
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email("abc@naver.com")
                .password("different1!") // 잘못된 비밀번호
                .build();
        User user = User.builder()
                .userId("abc@naver.com")
                .password(passwordEncoder.encode("password1!"))
                .build();

        given(userRepository.findByUserId(loginRequestDto.getEmail()))
                .willReturn(Optional.of(user));

        assertThrows(InvalidPasswordException.class, () -> authService.login(loginRequestDto));

        then(userRepository).should(times(1)).findByUserId(any());
    }

}