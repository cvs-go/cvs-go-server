package com.cvsgo.repository;

import com.cvsgo.config.TestConfig;
import com.cvsgo.entity.Role;
import com.cvsgo.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestConfig.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("사용자를 추가한다")
    void succeed_to_save_user() {
        // given
        User user = User.builder()
                .userId("abc@naver.com")
                .password("12345678a!")
                .nickname("닉네임")
                .role(Role.ASSOCIATE)
                .build();

        // when
        userRepository.save(user);

        // then
        assertThat(user.getId()).isNotNull();
    }

    @Test
    @DisplayName("닉네임으로 사용자를 찾는다")
    void succeed_to_find_user_by_nickname() {
        // given
        User user = User.builder()
                .userId("abc@naver.com")
                .password("12345678a!")
                .nickname("닉네임")
                .role(Role.ASSOCIATE)
                .build();
        userRepository.save(user);

        // when
        Optional<User> foundUser = userRepository.findByNickname(user.getNickname());

        // then
        assertThat(foundUser).isPresent();
    }

    @Test
    @DisplayName("이메일로 사용자를 찾는다")
    void succeed_to_find_user_by_email() {
        // given
        User user = User.builder()
                .userId("abc@naver.com")
                .password("12345678a!")
                .nickname("닉네임")
                .role(Role.ASSOCIATE)
                .build();
        userRepository.save(user);

        // when
        Optional<User> foundUser = userRepository.findByUserId(user.getUserId());

        // then
        assertThat(foundUser).isPresent();
    }

}
