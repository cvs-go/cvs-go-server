package com.cvsgo.repository;

import com.cvsgo.config.TestConfig;
import com.cvsgo.entity.Role;
import com.cvsgo.entity.User;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
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

    User user;

    @BeforeEach
    void initData() {
        user = User.create("abc@naver.com", "password1!", "닉네임", new ArrayList<>());
        userRepository.save(user);
    }

    @Test
    @DisplayName("사용자를 추가한다")
    void succeed_to_save_user() {
        User anotherUser = User.create("abcd@naver.com", "password1!", "닉네임2", new ArrayList<>());
        userRepository.save(anotherUser);

        assertThat(anotherUser.getId()).isNotNull();
    }

    @Test
    @DisplayName("ID를 통해 사용자를 찾는다")
    void succeed_to_find_user_by_id() {
        Optional<User> foundUser = userRepository.findById(user.getId());

        assertThat(foundUser).isPresent();
    }

    @Test
    @DisplayName("닉네임으로 사용자를 찾는다")
    void succeed_to_find_user_by_nickname() {
        Optional<User> foundUser = userRepository.findByNickname(user.getNickname());

        assertThat(foundUser).isPresent();
    }

    @Test
    @DisplayName("이메일로 사용자를 찾는다")
    void succeed_to_find_user_by_email() {
        Optional<User> foundUser = userRepository.findByUserId(user.getUserId());

        assertThat(foundUser).isPresent();
    }

}
