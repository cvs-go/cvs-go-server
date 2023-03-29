package com.cvsgo.controller;

import com.cvsgo.argumentresolver.LoginUserArgumentResolver;
import com.cvsgo.config.WebConfig;
import com.cvsgo.dto.review.ReviewSortBy;
import com.cvsgo.dto.review.SearchReviewRequestDto;
import com.cvsgo.dto.review.SearchReviewResponseDto;
import com.cvsgo.interceptor.AuthInterceptor;
import com.cvsgo.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
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
import org.springframework.restdocs.payload.JsonFieldType;
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

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
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

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String CREATE_REVIEW_API_PATH = "/api/products/{productId}/reviews";

    private static final String SEARCH_REVIEW_API_PATH = "/api/reviews";

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

    @Test
    @DisplayName("리뷰 조회에 성공하면 HTTP 200을 응답한다.")
    void respond_200_when_success_to_read_reviews() throws Exception {
        SearchReviewRequestDto requestDto = SearchReviewRequestDto.builder()
            .sortBy(ReviewSortBy.LIKE)
            .categoryIds(List.of(1L, 2L, 3L))
            .tagIds(List.of(2L))
            .ratings(List.of(4, 5))
            .build();

        given(reviewService.getReviewList(any(), any(), any()))
            .willReturn(List.of(responseDto1, responseDto2));

        mockMvc.perform(get(SEARCH_REVIEW_API_PATH)
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().isOk()).andDo(print())
            .andDo(document(documentIdentifier,
                getDocumentRequest(),
                getDocumentResponse(),
                requestFields(
                    fieldWithPath("sortBy").type(JsonFieldType.STRING).description("정렬 기준").optional(),
                    fieldWithPath("categoryIds").type(JsonFieldType.ARRAY).description("상품 카테고리 ID 목록").optional(),
                    fieldWithPath("tagIds").type(JsonFieldType.ARRAY).description("태그 ID 목록").optional(),
                    fieldWithPath("ratings").type(JsonFieldType.ARRAY).description("별점 목록").optional()
                ),
                relaxedResponseFields(
                    fieldWithPath("data[].productId").type(JsonFieldType.NUMBER).description("상품 ID"),
                    fieldWithPath("data[].productName").type(JsonFieldType.STRING).description("상품명"),
                    fieldWithPath("data[].productManufacturer").type(JsonFieldType.STRING).description("상품 제조사"),
                    fieldWithPath("data[].productImageUrl").type(JsonFieldType.STRING).description("상품 이미지 URL"),
                    fieldWithPath("data[].reviewerId").type(JsonFieldType.NUMBER).description("리뷰 작성자 ID"),
                    fieldWithPath("data[].reviewerNickname").type(JsonFieldType.STRING).description("리뷰 작성자 닉네임"),
                    fieldWithPath("data[].reviewerProfileImageUrl").type(JsonFieldType.STRING).description("리뷰 작성자 프로필 이미지 URL"),
                    fieldWithPath("data[].reviewerTags").type(JsonFieldType.ARRAY).description("리뷰 작성자의 태그 목록"),
                    fieldWithPath("data[].reviewLikeCount").type(JsonFieldType.NUMBER).description("리뷰 좋아요 개수"),
                    fieldWithPath("data[].rating").type(JsonFieldType.NUMBER).description("리뷰 별점"),
                    fieldWithPath("data[].reviewContent").type(JsonFieldType.STRING).description("리뷰 내용"),
                    fieldWithPath("data[].isReviewLiked").type(JsonFieldType.BOOLEAN).description("사용자의 리뷰 좋아요 여부"),
                    fieldWithPath("data[].isProductBookmarked").type(JsonFieldType.BOOLEAN).description("사용자의 상품 북마크 여부"),
                    fieldWithPath("data[].reviewImageUrls").type(JsonFieldType.ARRAY).description("리뷰 이미지 URL 목록").optional(),
                    fieldWithPath("data[].createdAt").type(JsonFieldType.STRING).description("리뷰 생성 시간")
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

    SearchReviewResponseDto responseDto1 = SearchReviewResponseDto.builder()
        .productId(13L)
        .productName("불닭볶음면큰컵")
        .productManufacturer("삼양")
        .productImageUrl("https://어쩌구저쩌구/product/불닭볶음면.jpg")
        .reviewerId(3L)
        .reviewerNickname("불닭러버")
        .reviewerProfileImageUrl("https://어쩌구저쩌구/user/불닭러버.jpg")
        .rating(5)
        .reviewContent("맛있어요")
        .reviewLikeCount(3L)
        .isProductBookmarked(false)
        .isReviewLiked(true)
        .reviewerTags(List.of("맵부심", "초코러버", "소식가"))
        .createdAt(LocalDateTime.now())
        .build();

    SearchReviewResponseDto responseDto2 = SearchReviewResponseDto.builder()
        .productId(13L)
        .productName("바질크림불닭우동")
        .productManufacturer("삼양")
        .productImageUrl("https://어쩌구저쩌구/product/바질크림불닭우동.jpg")
        .reviewerId(3L)
        .reviewerNickname("불닭러버")
        .reviewerProfileImageUrl("https://어쩌구저쩌구/user/불닭러버.jpg")
        .rating(5)
        .reviewContent("이번에 신제품 출시되었다고 해서 출시되자마자 먹어봤는데 생각보다 훨씬 맛있었어요")
        .reviewImageUrls(List.of("https://어쩌구저쩌구/review/신제품너무맛있다.jpg"))
        .reviewLikeCount(1L)
        .isProductBookmarked(true)
        .isReviewLiked(false)
        .reviewerTags(List.of("맵부심", "초코러버", "소식가"))
        .createdAt(LocalDateTime.now())
        .build();

}
