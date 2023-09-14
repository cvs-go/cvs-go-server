package com.cvsgo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.cvsgo.dto.user.SignUpRequestDto;
import com.cvsgo.dto.user.UpdateUserRequestDto;
import com.cvsgo.entity.Review;
import com.cvsgo.entity.Role;
import com.cvsgo.entity.Tag;
import com.cvsgo.entity.User;
import com.cvsgo.entity.UserFollow;
import com.cvsgo.entity.UserTag;
import com.cvsgo.exception.BadRequestException;
import com.cvsgo.exception.DuplicateException;
import com.cvsgo.exception.NotFoundException;
import com.cvsgo.repository.ReviewRepository;
import com.cvsgo.repository.TagRepository;
import com.cvsgo.repository.UserFollowRepository;
import com.cvsgo.repository.UserRepository;
import com.cvsgo.repository.UserTagRepository;
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
    private UserTagRepository userTagRepository;

    @Mock
    private UserFollowRepository userFollowRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EntityManager entityManager;

    @Test
    @DisplayName("이미 존재하는 닉네임이면 회원가입시 DuplicateException이 발생한다")
    void should_throw_DuplicateException_when_create_user_but_nickname_is_duplicate() {
        final String nickname = "닉네임";
        SignUpRequestDto signUpRequest = SignUpRequestDto.builder()
            .email("abc@naver.com")
            .password("111111111a!")
            .nickname(nickname)
            .tagIds(Arrays.asList(1L, 2L, 3L))
            .build();

        given(userRepository.findByNickname(nickname)).willReturn(Optional.of(user));
        given(userRepository.save(any())).willThrow(DataIntegrityViolationException.class);

        assertThrows(DuplicateException.class, () -> userService.signUp(signUpRequest));
        then(userRepository).should(times(1)).findByNickname(nickname);
    }

    @Test
    @DisplayName("이미 존재하는 이메일이면 회원가입시 DuplicateException이 발생한다")
    void should_throw_DuplicateException_when_create_user_email_is_duplicate() {
        final String email = "abc@naver.com";
        SignUpRequestDto signUpRequest = SignUpRequestDto.builder()
            .email(email)
            .password("111111111a!")
            .nickname("닉네임")
            .tagIds(Arrays.asList(1L, 2L, 3L))
            .build();

        given(userRepository.findByUserId(email)).willReturn(Optional.of(user));
        given(userRepository.save(any())).willThrow(DataIntegrityViolationException.class);

        assertThrows(DuplicateException.class, () -> userService.signUp(signUpRequest));
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
    @DisplayName("사용자 정보를 조회한다")
    void succeed_to_read_user() {
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(reviewRepository.findAllByUser(any())).willReturn(List.of(review1));

        userService.readUser(user.getId());

        then(userRepository).should(times(1)).findById(user.getId());
        then(reviewRepository).should(times(1)).findAllByUser(any());
    }

    @Test
    @DisplayName("사용자 정보 조회 시 해당하는 아이디를 가진 사용자가 없으면 NotFoundException이 발생한다")
    void should_throw_NotFoundException_when_read_user_but_user_does_not_exist() {
        final Long userId = 10000L;

        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.readUser(userId));
        then(userRepository).should(times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("회원을 정상적으로 수정한다")
    void succeed_to_update_user() {
        String newNickname = "수정닉네임";
        List<Tag> newTags = List.of(tag1, tag2);
        UpdateUserRequestDto request = new UpdateUserRequestDto(newNickname, List.of(1L, 3L),
            "프로필 이미지 URL");

        given(userRepository.findByNickname(request.getNickname())).willReturn(Optional.empty());
        given(tagRepository.findAllById(List.of(tag1.getId(), tag2.getId()))).willReturn(newTags);

        String beforeNickname = user.getNickname();
        userService.updateUser(user, request);
        String afterNickname = user.getNickname();

        then(userRepository).should(times(1)).findByNickname(request.getNickname());
        then(tagRepository).should(times(1)).findAllById(List.of(tag1.getId(), tag2.getId()));
        assertThat(beforeNickname).isNotEqualTo(user.getNickname());
        assertThat(afterNickname).isEqualTo(user.getNickname());
        assertThat(user.getUserTags()).hasSize(newTags.size());
    }

    @Test
    @DisplayName("이미 존재하는 닉네임이면 회원 수정 시 DuplicateException이 발생한다")
    void should_throw_DuplicateException_when_update_user_but_nickname_is_duplicate() {
        final String duplicatedNickname = user2.getNickname();
        UpdateUserRequestDto request = new UpdateUserRequestDto(duplicatedNickname, List.of(1L, 3L),
            "프로필 이미지 URL");

        given(userRepository.findByNickname(request.getNickname())).willReturn(Optional.of(user2));

        assertThrows(DuplicateException.class, () -> userService.updateUser(user, request));
        then(userRepository).should(times(1)).findByNickname(request.getNickname());
    }

    @Test
    @DisplayName("회원 팔로우를 정상적으로 생성한다")
    void succeed_to_create_user_follow() {
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user2));
        given(userFollowRepository.existsByUserAndFollower(any(), any())).willReturn(false);
        given(userFollowRepository.save(any())).willReturn(any());

        userService.createUserFollow(user, user2.getId());

        then(userRepository).should(times(1)).findById(user2.getId());
        then(userFollowRepository).should(times(1)).existsByUserAndFollower(any(), any());
        then(userFollowRepository).should(times(1)).save(any());
    }

    @Test
    @DisplayName("회원 팔로우 생성 시 해당하는 아이디를 가진 사용자가 없으면 NotFoundException이 발생한다")
    void should_throw_NotFoundException_when_create_user_follow_but_user_does_not_exist() {
        final Long followingId = 10000L;

        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.createUserFollow(user, followingId));
        then(userRepository).should(times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("회원 팔로우 생성 시 본인을 팔로우하는 경우 BadRequestException 발생한다")
    void should_throw_BadRequestException_when_create_user_follow_but_invalid_follow() {
        final Long followingId = user.getId();

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> userService.createUserFollow(user, followingId));
        then(userRepository).should(times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("회원 팔로우 생성 시 이미 존재하는 회원 팔로우면 DuplicateException이 발생한다")
    void should_throw_DuplicateException_when_create_user_follow_but_user_follow_duplicated() {
        final Long followingId = user2.getId();

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user2));
        given(userFollowRepository.existsByUserAndFollower(any(), any())).willReturn(true);

        assertThrows(DuplicateException.class, () -> userService.createUserFollow(user, followingId));
        then(userRepository).should(times(1)).findById(anyLong());
        then(userFollowRepository).should(times(1)).existsByUserAndFollower(any(), any());
    }

    @Test
    @DisplayName("회원 팔로우를 정상적으로 삭제한다")
    void succeed_to_delete_user_follow() {
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user2));
        given(userFollowRepository.findByUserAndFollower(any(), any())).willReturn(
            Optional.of(userFollow));

        userService.deleteUserFollow(user, user2.getId());

        then(userRepository).should(times(1)).findById(anyLong());
        then(userFollowRepository).should(times(1)).findByUserAndFollower(any(), any());
        then(userFollowRepository).should(times(1)).delete(any());
    }

    @Test
    @DisplayName("회원 팔로우 삭제 시 해당 ID의 회원이 없는 경우 NotFoundException이 발생한다")
    void should_throw_NotFoundException_when_delete_user_follow_but_user_does_not_exist() {
        given(userRepository.findById(anyLong())).willThrow(NotFoundException.class);

        assertThrows(NotFoundException.class,
            () -> userService.deleteUserFollow(user, 1000L));

        then(userRepository).should(times(1)).findById(any());
    }

    @Test
    @DisplayName("회원 팔로우 삭제 시 해당하는 회원 팔로우가 없는 경우 NotFoundException이 발생한다")
    void should_throw_NotFoundException_when_delete_user_follow_but_user_follow_does_not_exist() {
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(userFollowRepository.findByUserAndFollower(any(), any())).willReturn(
            Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.deleteUserFollow(user, 1L));

        then(userRepository).should(times(1)).findById(any());
        then(userFollowRepository).should(times(1)).findByUserAndFollower(any(), any());
    }

    @Test
    @DisplayName("태그 매칭률을 조회한다")
    void succeed_to_read_user_tag_match_percentage() {
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user2));
        given(userTagRepository.findAllByUser(any())).willReturn(List.of(userTag1, userTag2));
        given(userTagRepository.findAllByUser(any())).willReturn(List.of(userTag3, userTag4));

        userService.readUserTagMatchPercentage(user, user2.getId());

        then(userRepository).should(times(1)).findById(user2.getId());
        then(userTagRepository).should(times(2)).findAllByUser(any());
    }

    @Test
    @DisplayName("태그 매칭률 조회 시 해당하는 아이디를 가진 사용자가 없으면 NotFoundException이 발생한다")
    void should_throw_NotFoundException_when_read_user_tag_match_percentage_but_user_does_not_exist() {
        final Long userId = 10000L;

        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.readUserTagMatchPercentage(user, userId));
        then(userRepository).should(times(1)).findById(anyLong());
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

    Tag tag1 = Tag.builder()
        .id(1L)
        .name("맵찔이")
        .group(1)
        .build();

    Tag tag2 = Tag.builder()
        .id(3L)
        .name("초코러버")
        .group(2)
        .build();

    Tag tag3 = Tag.builder()
        .id(5L)
        .name("다이어터")
        .group(4)
        .build();

    UserTag userTag1 = UserTag.builder()
        .user(user)
        .tag(tag1)
        .build();

    UserTag userTag2 = UserTag.builder()
        .user(user)
        .tag(tag3)
        .build();

    UserTag userTag3 = UserTag.builder()
        .user(user2)
        .tag(tag2)
        .build();

    UserTag userTag4 = UserTag.builder()
        .user(user2)
        .tag(tag3)
        .build();

    Review review1 = Review.builder()
        .user(user)
        .build();

    Review review2 = Review.builder()
        .user(user2)
        .build();

    UserFollow userFollow = UserFollow.create(user2, user);

}
