package com.cvsgo.repository;

import com.cvsgo.config.TestConfig;
import com.cvsgo.entity.Tag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestConfig.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TagRepositoryTest {

    @Autowired
    TagRepository tagRepository;

    @Test
    @DisplayName("태그를 조회한다")
    void find_tags() {
        // given
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
        List<Tag> savedTags = tagRepository.saveAll(List.of(tag1, tag2, tag3, tag4, tag5, tag6, tag7));

        // when
        List<Tag> foundTags = tagRepository.findAll();

        // then
        assertThat(foundTags.size()).isEqualTo(savedTags.size());

    }
}
