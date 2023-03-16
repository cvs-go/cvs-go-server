package com.cvsgo.service;

import com.cvsgo.entity.Tag;
import com.cvsgo.repository.TagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @InjectMocks
    TagService tagService;

    @Mock
    private TagRepository tagRepository;

    @Test
    @DisplayName("태그 목록을 정상적으로 조회한다")
    void succeed_to_read_tag_list() {
        given(tagRepository.findAll()).willReturn(getTagList());

        List<Tag> tagList = tagRepository.findAll();

        assertEquals(getTagList().size(), tagList.size());
        then(tagRepository)
                .should(times(1)).findAll();
    }

    private static List<Tag> getTagList() {
        Tag tag1 = Tag.builder()
                .id(1L)
                .name("맵찔이")
                .group(1)
                .build();
        Tag tag2 = Tag.builder()
                .id(2L)
                .name("맵부심")
                .group(1)
                .build();
        Tag tag3 = Tag.builder()
                .id(3L)
                .name("초코러버")
                .group(2)
                .build();
        return List.of(tag1, tag2, tag3);
    }

}
