package com.cvsgo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.cvsgo.config.TestConfig;
import com.cvsgo.entity.Tag;
import com.cvsgo.entity.User;
import com.cvsgo.entity.UserTag;
import java.util.List;
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
class UserTagRepositoryTest {

    @Autowired
    private UserTagRepository userTagRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    User user;

    @BeforeEach
    void initData() {
        tag1 = Tag.builder().name("맵찔이").group(1).build();
        tag2 = Tag.builder().name("맵부심").group(1).build();
        tag3 = Tag.builder().name("초코러버").group(2).build();
        tag4 = Tag.builder().name("비건").group(3).build();
        tag5 = Tag.builder().name("다이어터").group(4).build();
        tag6 = Tag.builder().name("대식가").group(5).build();
        tag7 = Tag.builder().name("소식가").group(5).build();
        tagRepository.saveAll(List.of(tag1, tag2, tag3, tag4, tag5, tag6, tag7));

        user1 = User.create("abc@naver.com", "password1!", "닉네임1", List.of(tag1, tag3, tag6));
        user2 = User.create("xyz@gmail.com", "password1!", "닉네임2", List.of(tag2, tag4, tag6));
        userRepository.saveAll(List.of(user1, user2));
    }

    @Test
    @DisplayName("유저 태그를 조회한다")
    void find_user_tags_by_user() {
        // when
        List<UserTag> userTags = userTagRepository.findAllByUser(user1);

        // then
        List<Tag> tags = userTags.stream().map(UserTag::getTag).toList();
        assertThat(userTags).hasSize(3);
        assertThat(tags).extracting("name").containsExactly("맵찔이", "초코러버", "대식가");
        assertThat(tags).extracting("name").doesNotContain("맵부심", "비건", "다이어터", "소식가");
    }

    private Tag tag1;
    private Tag tag2;
    private Tag tag3;
    private Tag tag4;
    private Tag tag5;
    private Tag tag6;
    private Tag tag7;
    private User user1;
    private User user2;
}
