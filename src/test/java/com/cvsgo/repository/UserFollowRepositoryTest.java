package com.cvsgo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.cvsgo.config.TestConfig;
import com.cvsgo.entity.ProductBookmark;
import com.cvsgo.entity.User;
import com.cvsgo.entity.UserFollow;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Import(TestConfig.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserFollowRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFollowRepository userFollowRepository;

    User user1;
    User user2;
    UserFollow userFollow;

    @BeforeEach
    void initData() {
        user1 = User.create("abc@naver.com", "password1!", "닉네임1", new ArrayList<>());
        user2 = User.create("abcd@naver.com", "password1!", "닉네임2", new ArrayList<>());
        userRepository.saveAll(List.of(user1, user2));

        userFollow = UserFollow.create(user2, user1);
        userFollowRepository.save(userFollow);
    }

    @Test
    @DisplayName("회원 팔로우를 추가한다")
    void succeed_to_save_user_follow() {
        UserFollow anotherUserFollow = UserFollow.create(user1, user2);
        userFollowRepository.save(anotherUserFollow);

        assertThat(anotherUserFollow.getId()).isNotNull();
    }

    @Test
    @DisplayName("회원 팔로우를 삭제한다")
    void succeed_to_delete_product_bookmark() {
        Optional<UserFollow> foundUserFollow = userFollowRepository.findByUserAndFollower(user2,
            user1);

        foundUserFollow.ifPresent(
            selectUserFollow -> userFollowRepository.delete(selectUserFollow));
        Optional<UserFollow> deletedUserFollow = userFollowRepository.findByUserAndFollower(user2,
            user1);

        assertThat(deletedUserFollow).isNotPresent();
    }

    @Test
    @DisplayName("회원 팔로우가 있는지 조회한다")
    void succeed_to_check_user_follow_existence() {
        Boolean result1 = userFollowRepository.existsByUserAndFollower(user2, user1);
        Boolean result2 = userFollowRepository.existsByUserAndFollower(user1, user2);

        assertThat(result1).isTrue();
        assertThat(result2).isFalse();
    }

    @Test
    @DisplayName("회원 팔로우가 있는지 조회한다")
    void succeed_to_find_user_follow_by_user_and_follower() {
        Optional<UserFollow> userFollow1 = userFollowRepository.findByUserAndFollower(user2, user1);
        Optional<UserFollow> userFollow2 = userFollowRepository.findByUserAndFollower(user1, user2);

        assertThat(userFollow1).isPresent();
        assertThat(userFollow2).isNotPresent();
    }

}
