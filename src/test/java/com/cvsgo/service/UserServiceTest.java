package com.cvsgo.service;

import com.cvsgo.dto.user.SignUpRequestDto;
import com.cvsgo.entity.User;
import com.cvsgo.exception.user.DuplicateEmailException;
import com.cvsgo.exception.user.DuplicateNicknameException;
import com.cvsgo.repository.TagRepository;
import com.cvsgo.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("이미 존재하는 닉네임이면 회원가입시 DuplicateNicknameException이 발생한다")
    void should_throw_DuplicateEmailException_when_nickname_is_duplicate() {
        final String nickname = "닉네임";
        SignUpRequestDto signUpRequest = SignUpRequestDto.builder()
                .email("abc@naver.com")
                .password("111111111a!")
                .nickname(nickname)
                .tagIds(Arrays.asList(1L, 2L, 3L))
                .build();

        // given
        given(userRepository.findByNickname(nickname))
                .willReturn(Optional.empty())
                .willReturn(Optional.of(User.builder().build()));
        given(userRepository.save(any()))
                .willReturn(User.builder().build());

        // when
        userService.signUp(signUpRequest);

        // then
        assertThrows(DuplicateNicknameException.class, () -> userService.signUp(signUpRequest));
        then(userRepository)
                .should(times(2)).findByNickname(nickname);
    }


    @Test
    @DisplayName("이미 존재하는 이메일이면 회원가입시 DuplicateEmailException이 발생한다")
    void should_throw_DuplicateNicknameException_when_email_is_duplicate() {
        final String email = "abcd@naver.com";
        SignUpRequestDto signUpRequest = SignUpRequestDto.builder()
                .email(email)
                .password("111111111a!")
                .nickname("닉네임")
                .tagIds(Arrays.asList(1L, 2L, 3L))
                .build();

        // given
        given(userRepository.findByUserId(email))
                .willReturn(Optional.empty())
                .willReturn(Optional.of(User.builder().build()));
        given(userRepository.save(any()))
                .willReturn(User.builder().build());

        // when
        userService.signUp(signUpRequest);

        // then
        assertThrows(DuplicateEmailException.class, () -> userService.signUp(signUpRequest));
        then(userRepository)
                .should(times(2)).findByUserId(email);
    }

    @Test
    @DisplayName("이미 존재하는 이메일이면 이메일 중복 체크시 true를 반환한다")
    void should_return_true_when_email_exists() {
        final String email = "abc@naver.com";

        // given
        given(userRepository.findByUserId(email))
                .willReturn(Optional.of(User.builder().build()));

        // when
        boolean result = userService.isDuplicatedEmail(email);

        // then
        assertTrue(result);
        then(userRepository)
                .should(atLeastOnce()).findByUserId(email);
    }

    @Test
    @DisplayName("존재하지 않는 이메일이면 이메일 중복 체크시 false를 반환한다")
    void should_return_false_when_email_does_not_exist() {
        final String email = "abc@naver.com";

        // given
        given(userRepository.findByUserId(email))
                .willReturn(Optional.empty());

        // when
        boolean result = userService.isDuplicatedEmail(email);

        // then
        assertFalse(result);
        then(userRepository)
                .should(atLeastOnce()).findByUserId(email);
    }

    @Test
    @DisplayName("이미 존재하는 닉네임이면 닉네임 중복 체크시 true를 반환한다")
    void should_return_true_when_nickname_exists() {
        final String nickname = "닉네임";

        // given
        given(userRepository.findByNickname(nickname))
                .willReturn(Optional.of(User.builder().build()));

        // when
        boolean result = userService.isDuplicatedNickname(nickname);

        // then
        assertTrue(result);
        then(userRepository)
                .should(atLeastOnce()).findByNickname(nickname);
    }

    @Test
    @DisplayName("존재하지 않는 닉네임이면 닉네임 중복 체크시 false를 반환한다")
    void should_return_false_when_nickname_does_not_exist() {
        final String nickname = "닉네임";

        // given
        given(userRepository.findByNickname(nickname))
                .willReturn(Optional.empty());

        // when
        boolean result = userService.isDuplicatedNickname(nickname);

        // then
        assertFalse(result);
        then(userRepository)
                .should(atLeastOnce()).findByNickname(nickname);
    }

}
