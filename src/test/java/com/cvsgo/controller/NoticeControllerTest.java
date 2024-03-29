package com.cvsgo.controller;

import static com.cvsgo.ApiDocumentUtils.documentIdentifier;
import static com.cvsgo.ApiDocumentUtils.getDocumentRequest;
import static com.cvsgo.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

import com.cvsgo.argumentresolver.LoginUserArgumentResolver;
import com.cvsgo.config.WebConfig;
import com.cvsgo.dto.notice.ReadNoticeDetailResponseDto;
import com.cvsgo.dto.notice.ReadNoticeResponseDto;
import com.cvsgo.entity.Notice;
import com.cvsgo.entity.NoticeImage;
import com.cvsgo.interceptor.AuthInterceptor;
import com.cvsgo.service.NoticeService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(NoticeController.class)
class NoticeControllerTest {

    @MockBean
    LoginUserArgumentResolver loginUserArgumentResolver;

    @MockBean
    WebConfig webConfig;

    @MockBean
    AuthInterceptor authInterceptor;

    @MockBean
    private NoticeService noticeService;

    private MockMvc mockMvc;

    @BeforeEach
    void setup(WebApplicationContext webApplicationContext,
        RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(documentationConfiguration(restDocumentation))
            .apply(sharedHttpSession())
            .addFilters(new CharacterEncodingFilter("UTF-8", true))
            .build();
    }

    @Test
    @DisplayName("공지사항 목록을 정상적으로 조회하면 HTTP 200을 응답한다")
    void respond_200_when_read_notice_list_successfully() throws Exception {
        List<ReadNoticeResponseDto> responseDto = List.of(new ReadNoticeResponseDto(notice1), new ReadNoticeResponseDto(notice2));
        given(noticeService.readNoticeList(any())).willReturn(new PageImpl<>(responseDto));

        mockMvc.perform(get("/api/notices").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document(documentIdentifier,
                getDocumentRequest(),
                getDocumentResponse(),
                relaxedResponseFields(
                    fieldWithPath("data.content[].id").type(JsonFieldType.NUMBER).description("공지사항 ID"),
                    fieldWithPath("data.content[].title").type(JsonFieldType.STRING).description("공지사항 제목"),
                    fieldWithPath("data.content[].createdAt").type(JsonFieldType.STRING).description("공지사항 생성 시간").optional(),
                    fieldWithPath("data.content[].isNew").type(JsonFieldType.BOOLEAN).description("새 공지사항 여부").optional()
                )
            ));
    }

    @Test
    @DisplayName("공지사항을 정상적으로 조회하면 HTTP 200을 응답한다")
    void respond_200_when_read_notice_successfully() throws Exception {
        ReadNoticeDetailResponseDto responseDto = ReadNoticeDetailResponseDto.of(notice1, List.of(noticeImage.getImageUrl()));
        given(noticeService.readNotice(any())).willReturn(responseDto);

        mockMvc.perform(get("/api/notices/{noticeId}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document(documentIdentifier,
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(
                    parameterWithName("noticeId").description("공지사항 ID")
                ),
                relaxedResponseFields(
                    fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("공지사항 ID"),
                    fieldWithPath("data.title").type(JsonFieldType.STRING).description("공지사항 제목"),
                    fieldWithPath("data.content").type(JsonFieldType.STRING).description("공지사항 내용"),
                    fieldWithPath("data.noticeImageUrls").type(JsonFieldType.ARRAY).description("공지사항 이미지 URL").optional(),
                    fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("공지사항 생성 시간").optional()
                )
            ));
    }

    Notice notice1 = Notice.builder()
        .id(1L)
        .title("긴급 공지")
        .content("긴급 상황입니다")
        .build();

    Notice notice2 = Notice.builder()
        .id(2L)
        .title("이벤트 공지")
        .content("이벤트 배너를 확인하세요")
        .build();

    NoticeImage noticeImage = NoticeImage.builder()
        .id(1L)
        .imageUrl("imageUrl")
        .notice(notice1)
        .build();

}
