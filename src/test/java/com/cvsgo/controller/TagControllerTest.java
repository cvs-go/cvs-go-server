package com.cvsgo.controller;

import com.cvsgo.argumentresolver.LoginUserArgumentResolver;
import com.cvsgo.config.WebConfig;
import com.cvsgo.dto.tag.TagResponseDto;
import com.cvsgo.entity.Tag;
import com.cvsgo.interceptor.AuthInterceptor;
import com.cvsgo.service.TagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.List;
import java.util.stream.Stream;

import static com.cvsgo.ApiDocumentUtils.getDocumentRequest;
import static com.cvsgo.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(TagController.class)
class TagControllerTest {

    @MockBean
    private TagService tagService;

    private MockMvc mockMvc;

    @MockBean
    private AuthInterceptor authInterceptor;

    @MockBean
    LoginUserArgumentResolver loginUserArgumentResolver;

    @MockBean
    WebConfig webConfig;

    @BeforeEach
    void setup(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .apply(sharedHttpSession())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @Test
    @DisplayName("태그 목록을 정상적으로 조회하면 HTTP 200을 응답한다")
    void respond_200_when_read_tag_list_successfully() throws Exception {
        given(tagService.getTagList()).willReturn(getTagList());

        mockMvc.perform(get("/api/tags").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("tags/get",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("요청 시각"),
                                fieldWithPath("data").type(JsonFieldType.ARRAY).description("태그 목록"),
                                fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("태그 ID"),
                                fieldWithPath("data[].name").type(JsonFieldType.STRING).description("태그 이름"),
                                fieldWithPath("data[].group").type(JsonFieldType.NUMBER).description("태그 그룹")
                        )
                ));
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
        return Stream.of(tag1, tag2, tag3).map(TagResponseDto::from).toList();
    }

}
