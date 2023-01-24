package com.cvsgo.controller;

import com.cvsgo.dto.tag.TagResponseDto;
import com.cvsgo.entity.Tag;
import com.cvsgo.service.TagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

@WebMvcTest(TagController.class)
public class TagControllerTest {

    @MockBean
    private TagService tagService;

    private MockMvc mockMvc;

    @BeforeEach
    void setup(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(sharedHttpSession())
                .build();
    }

    @Test
    @DisplayName("태그 목록을 정상적으로 조회하면 HTTP 200을 응답한다")
    void respond_200_when_read_tag_list_successfully() throws Exception {
        given(tagService.getTagList()).willReturn(getTagList());

        mockMvc.perform(get("/api/tags").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    private static List<TagResponseDto> getTagList() {
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
        return Stream.of(tag1, tag2, tag3).map(TagResponseDto::new).toList();
    }

}
