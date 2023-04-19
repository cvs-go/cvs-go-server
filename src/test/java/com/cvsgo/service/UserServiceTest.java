package com.cvsgo.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.cvsgo.dto.user.SignUpRequestDto;
import com.cvsgo.entity.Role;
import com.cvsgo.entity.User;
import com.cvsgo.entity.UserFollow;
import com.cvsgo.exception.auth.NotFoundUserException;
import com.cvsgo.exception.user.BadRequestUserFollowException;
import com.cvsgo.exception.user.DuplicateEmailException;
import com.cvsgo.exception.user.DuplicateNicknameException;
import com.cvsgo.exception.user.DuplicateUserFollowException;
import com.cvsgo.repository.TagRepository;
import com.cvsgo.repository.UserFollowRepository;
import com.cvsgo.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private UserFollowRepository userFollowRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EntityManager entityManager;

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

        given(userRepository.findByNickname(nickname)).willReturn(Optional.of(user));
        given(userRepository.save(any())).willThrow(DataIntegrityViolationException.class);

        assertThrows(DuplicateNicknameException.class, () -> userService.signUp(signUpRequest));
        then(userRepository).should(times(1)).findByNickname(nickname);
    }


    @Test
    @DisplayName("이미 존재하는 이메일이면 회원가입시 DuplicateEmailException이 발생한다")
    void should_throw_DuplicateNicknameException_when_email_is_duplicate() {
        final String email = "abc@naver.com";
        SignUpRequestDto signUpRequest = SignUpRequestDto.builder()
            .email(email)
            .password("111111111a!")
            .nickname("닉네임")
            .tagIds(Arrays.asList(1L, 2L, 3L))
            .build();

        given(userRepository.findByUserId(email)).willReturn(Optional.of(user));
        given(userRepository.save(any())).willThrow(DataIntegrityViolationException.class);

        assertThrows(DuplicateEmailException.class, () -> userService.signUp(signUpRequest));
        then(userRepository).should(times(1)).findByUserId(email);
    }

    @Test
    @DisplayName("이미 존재하는 이메일이면 이메일 중복 체크시 true를 반환한다")
    void should_return_true_when_email_exists() {
        final String email = "abc@naver.com";

        // given
        given(userRepository.findByUserId(email))
            .willReturn(Optional.of(user));

        // when
        boolean result = userService.isDuplicatedEmail(email);

        // then
        assertTrue(result);
        then(userRepository).should(times(1)).findByUserId(email);
    }

    @Test
    @DisplayName("존재하지 않는 이메일이면 이메일 중복 체크시 false를 반환한다")
    void should_return_false_when_email_does_not_exist() {
        final String email = "abc@naver.com";

        // given
        given(userRepository.findByUserId(email)).willReturn(Optional.empty());

        // when
        boolean result = userService.isDuplicatedEmail(email);

        // then
        assertFalse(result);
        then(userRepository).should(times(1)).findByUserId(email);
    }

    @Test
    @DisplayName("이미 존재하는 닉네임이면 닉네임 중복 체크시 true를 반환한다")
    void should_return_true_when_nickname_exists() {
        final String nickname = "닉네임";

        // given
        given(userRepository.findByNickname(nickname))
            .willReturn(Optional.of(user));

        // when
        boolean result = userService.isDuplicatedNickname(nickname);

        // then
        assertTrue(result);
        then(userRepository).should(times(1)).findByNickname(nickname);
    }

    @Test
    @DisplayName("존재하지 않는 닉네임이면 닉네임 중복 체크시 false를 반환한다")
    void should_return_false_when_nickname_does_not_exist() {
        final String nickname = "닉네임";

        // given
        given(userRepository.findByNickname(nickname)).willReturn(Optional.empty());

        // when
        boolean result = userService.isDuplicatedNickname(nickname);

        // then
        assertFalse(result);
        then(userRepository).should(times(1)).findByNickname(nickname);
    }

    @Test
    @DisplayName("회원 팔로우를 정상적으로 생성한다")
    void succeed_to_create_user_follow() {
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user2));
        given(userFollowRepository.save(any())).willReturn(any());

        userService.createUserFollow(user, user2.getId());

        then(userRepository).should(times(1)).findById(user2.getId());
        then(userFollowRepository).should(times(1)).save(any());
    }

    @Test
    @DisplayName("해당하는 아이디를 가진 사용자가 없으면 NotFoundUserException이 발생한다")
    void should_throw_NotFoundUserException_when_user_does_not_exist() {
        final Long followingId = 10000L;

        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(NotFoundUserException.class, () -> userService.createUserFollow(user, followingId));
        then(userRepository).should(times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("본인을 팔로우하는 경우 BadRequestUserFollowException 발생한다")
    void should_throw_BadRequestUserFollowException_when_user_self_follow() {
        final Long followingId = user.getId();

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        assertThrows(BadRequestUserFollowException.class, () -> userService.createUserFollow(user, followingId));
        then(userRepository).should(times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("이미 존재하는 회원 팔로우면 팔로우 생성 시 DuplicateUserFollowException이 발생한다")
    void should_throw_DuplicateUserFollowException_when_user_follow_is_duplicate() {
        final Long followingId = user2.getId();

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user2));
        given(userFollowRepository.save(any())).willThrow(DataIntegrityViolationException.class);
        given(userFollowRepository.existsByUserAndFollower(any(), any())).willReturn(true);

        assertThrows(DuplicateUserFollowException.class, () -> userService.createUserFollow(user, followingId));
        then(userRepository).should(times(1)).findById(anyLong());
        then(userFollowRepository).should(times(1)).existsByUserAndFollower(any(), any());
    }

    User user = User.builder()
        .id(1L)
        .userId("abc@naver.com")
        .nickname("사용자1")
        .role(Role.ASSOCIATE)
        .build();

    User user2 = User.builder()
        .id(2L)
        .userId("abcd@naver.com")
        .nickname("사용자2")
        .role(Role.REGULAR)
        .build();

}
