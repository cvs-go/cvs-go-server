package com.cvsgo.controller;

import com.cvsgo.argumentresolver.LoginUserArgumentResolver;
import com.cvsgo.config.WebConfig;
import com.cvsgo.interceptor.AuthInterceptor;
import com.cvsgo.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.cvsgo.ApiDocumentUtils.documentIdentifier;
import static com.cvsgo.ApiDocumentUtils.getDocumentRequest;
import static com.cvsgo.ApiDocumentUtils.getDocumentResponse;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.io.IOException;

import static org.mockito.BDDMockito.willThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

    @MockBean
    private ReviewService reviewService;

    @MockBean
    LoginUserArgumentResolver loginUserArgumentResolver;

    @MockBean
    WebConfig webConfig;

    @MockBean
    AuthInterceptor authInterceptor;

    private MockMvc mockMvc;

    private static final String CREATE_REVIEW_API_PATH = "/api/products/{productId}/reviews";

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
    @DisplayName("리뷰 생성에 성공하면 HTTP 201을 응답한다.")
    void respond_201_when_success_to_create_review() throws Exception {

        MockMultipartFile image1 = new MockMultipartFile("images", "sample_image1.png",
            MediaType.IMAGE_PNG_VALUE, "image 1".getBytes());
        MockMultipartFile image2 = new MockMultipartFile("images", "sample_image2.png",
            MediaType.IMAGE_PNG_VALUE, "image 2".getBytes());

        mockMvc.perform(multipart(CREATE_REVIEW_API_PATH, 1)
                .file(image1)
                .file(image2)
                .param("content", "진짜 맛있어요")
                .param("rating", "5")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().isCreated()).andDo(print())
            .andDo(document(documentIdentifier,
                getDocumentRequest(),
                getDocumentResponse(),
                RequestDocumentation.pathParameters(
                    RequestDocumentation.parameterWithName("productId").description("상품 ID")
                ),
                RequestDocumentation.requestParts(
                    RequestDocumentation.partWithName("images").description("리뷰 이미지")
                ),
                RequestDocumentation.formParameters(
                    RequestDocumentation.parameterWithName("content").description("리뷰 내용"),
                    RequestDocumentation.parameterWithName("rating").description("별점")
                )
            ));
    }

    @ParameterizedTest
    @DisplayName("리뷰 별점이 1점 이상 5점 이하가 아니면 HTTP 400을 응답한다.")
    @ValueSource(ints = {0, 6, 7, 8, 9, 10, 100})
    void respond_400_when_rating_is_not_range_1_to_5(Integer rating) throws Exception {

        mockMvc.perform(multipart(CREATE_REVIEW_API_PATH, 1)
                .param("content", "맛있어요")
                .param("rating", String.valueOf(rating))
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("평점은 1점 이상 5점 이하여야 합니다")))
            .andDo(print());
    }

    @Test
    @DisplayName("리뷰 내용이 없으면 HTTP 400을 응답한다.")
    void respond_400_when_review_content_is_empty() throws Exception {

        mockMvc.perform(multipart(CREATE_REVIEW_API_PATH, 1)
                .param("content", "")
                .param("rating", "5")
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("리뷰는 1자 이상 1000자 이하여야 합니다.")))
            .andDo(print());

    }

    @Test
    @DisplayName("리뷰 내용이 1000글자를 초과하면 HTTP 400을 응답한다.")
    void respond_400_when_review_content_exceed_1000_letters() throws Exception {

        mockMvc.perform(multipart(CREATE_REVIEW_API_PATH, 1)
                .param("content", "내용".repeat(501))
                .param("rating", "5")
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("리뷰는 1자 이상 1000자 이하여야 합니다.")))
            .andDo(print());
    }

    @Test
    @DisplayName("리뷰 내용이 1000글자이면 HTTP 201을 응답한다.")
    void respond_201_when_review_content_does_not_exceed_1000_letters() throws Exception {

        mockMvc.perform(multipart(CREATE_REVIEW_API_PATH, 1)
                .param("content", "내용".repeat(500))
                .param("rating", "5")
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isCreated()).andDo(print());
    }


    @Test
    @DisplayName("IOException이 발생하면 HTTP 500을 응답한다.")
    void respond_500_when_IOException_occurs() throws Exception {

        willThrow(IOException.class).given(reviewService).createReview(any(), anyLong(), any());

        mockMvc.perform(multipart(CREATE_REVIEW_API_PATH, 1)
                .param("content", "맛있어요")
                .param("rating", "5")
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isInternalServerError())
            .andDo(print());
    }

}
