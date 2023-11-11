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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

import com.cvsgo.argumentresolver.LoginUserArgumentResolver;
import com.cvsgo.config.WebConfig;
import com.cvsgo.dto.promotion.ReadPromotionResponseDto;
import com.cvsgo.entity.Promotion;
import com.cvsgo.interceptor.AuthInterceptor;
import com.cvsgo.service.PromotionService;
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
@WebMvcTest(PromotionController.class)
class PromotionControllerTest {

    @MockBean
    LoginUserArgumentResolver loginUserArgumentResolver;

    @MockBean
    WebConfig webConfig;

    @MockBean
    AuthInterceptor authInterceptor;

    @MockBean
    private PromotionService promotionService;

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
    @DisplayName("프로모션 목록을 정상적으로 조회하면 HTTP 200을 응답한다")
    void respond_200_when_read_promotion_list_successfully() throws Exception {
        List<ReadPromotionResponseDto> responseDto = List.of(new ReadPromotionResponseDto(promotion1), new ReadPromotionResponseDto(promotion2));
        given(promotionService.readPromotionList(any())).willReturn(new PageImpl<>(responseDto));

        mockMvc.perform(get("/api/promotions").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document(documentIdentifier,
                getDocumentRequest(),
                getDocumentResponse(),
                relaxedResponseFields(
                    fieldWithPath("data.content[].id").type(JsonFieldType.NUMBER).description("프로모션 ID"),
                    fieldWithPath("data.content[].imageUrl").type(JsonFieldType.STRING).description("프로모션 이미지 url"),
                    fieldWithPath("data.content[].landingUrl").type(JsonFieldType.STRING).description("프로모션 랜딩 url")
                )
            ));
    }

    Promotion promotion1 = Promotion.builder()
        .id(1L)
        .imageUrl("imageUrl1")
        .landingUrl("landindUrl1")
        .build();

    Promotion promotion2 = Promotion.builder()
        .id(2L)
        .imageUrl("imageUrl2")
        .landingUrl("landindUrl2")
        .build();
}
