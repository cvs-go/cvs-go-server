package com.cvsgo.repository;

import com.cvsgo.entity.Role;
import com.cvsgo.entity.Tag;
import com.cvsgo.entity.User;
import com.cvsgo.entity.UserTag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserTagRepositoryTest {

    @Autowired
    private UserTagRepository userTagRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    @BeforeEach
    void setInitialData() {
        Tag tag1 = Tag.builder()
                .name("맵찔이")
                .group(1)
                .build();
        Tag tag2 = Tag.builder()
                .name("맵부심")
                .group(1)
                .build();
        Tag tag3 = Tag.builder()
                .name("초코러버")
                .group(2)
                .build();
        Tag tag4 = Tag.builder()
                .name("비건")
                .group(3)
                .build();
        Tag tag5 = Tag.builder()
                .name("다이어터")
                .group(4)
                .build();
        Tag tag6 = Tag.builder()
                .name("대식가")
                .group(5)
                .build();
        Tag tag7 = Tag.builder()
                .name("소식가")
                .group(5)
                .build();
        List<Tag> tags = List.of(tag1, tag2, tag3, tag4, tag5, tag6, tag7);
        tagRepository.saveAll(tags);
    }

    @Test
    @DisplayName("사용자 태그를 추가한다")
    void save_user_tag() {
        // given
        User user = User.builder()
                .userId("abc@naver.com")
                .password("12345678a!")
                .nickname("닉네임")
                .role(Role.ASSOCIATE)
                .build();
        User createdUser = userRepository.save(user);
        List<Tag> tags = tagRepository.findAllById(List.of(2L, 3L, 7L));
        List<UserTag> userTags = tags.stream().map(
                        tag -> UserTag.builder()
                                .user(createdUser)
                                .tag(tag)
                                .build())
                .toList();

        // when
        List<UserTag> savedUserTags = userTagRepository.saveAll(userTags);

        // then
        assertThat(savedUserTags.size()).isEqualTo(tags.size());
    }

}
