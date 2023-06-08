package com.cvsgo.controller;

import static com.cvsgo.ApiDocumentUtils.documentIdentifier;
import static com.cvsgo.ApiDocumentUtils.getDocumentRequest;
import static com.cvsgo.ApiDocumentUtils.getDocumentResponse;
import static com.cvsgo.exception.ExceptionConstants.DUPLICATE_REVIEW;
import static com.cvsgo.exception.ExceptionConstants.DUPLICATE_REVIEW_LIKE;
import static com.cvsgo.exception.ExceptionConstants.FORBIDDEN_REVIEW;
import static com.cvsgo.exception.ExceptionConstants.NOT_FOUND_REVIEW;
import static com.cvsgo.exception.ExceptionConstants.NOT_FOUND_REVIEW_LIKE;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

import com.cvsgo.argumentresolver.LoginUserArgumentResolver;
import com.cvsgo.config.WebConfig;
import com.cvsgo.dto.review.CreateReviewRequestDto;
import com.cvsgo.dto.review.ReadProductReviewQueryDto;
import com.cvsgo.dto.review.ReadProductReviewRequestDto;
import com.cvsgo.dto.review.ReadProductReviewResponseDto;
import com.cvsgo.dto.review.ReadReviewRequestDto;
import com.cvsgo.dto.review.ReadReviewResponseDto;
import com.cvsgo.dto.review.ReviewDto;
import com.cvsgo.dto.review.ReviewSortBy;
import com.cvsgo.dto.review.UpdateReviewRequestDto;
import com.cvsgo.entity.Review;
import com.cvsgo.entity.Role;
import com.cvsgo.entity.User;
import com.cvsgo.exception.ErrorCode;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

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

    private static final String PRODUCT_REVIEW_API_PATH = "/api/products/{productId}/reviews";

    private static final String UPDATE_REVIEW_API_PATH = "/api/reviews/{reviewId}";

    private static final String SEARCH_REVIEW_API_PATH = "/api/reviews";

    private static final String REVIEW_LIKE_API_PATH = "/api/reviews/{reviewId}/likes";

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
    void respond_201_when_succeed_to_create_review() throws Exception {
        CreateReviewRequestDto requestDto = CreateReviewRequestDto.builder()
            .rating(5)
            .content("맛있어요")
            .imageUrls(List.of("이미지 URL 1", "이미지 URL 2"))
            .build();

        mockMvc.perform(post(PRODUCT_REVIEW_API_PATH, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isCreated()).andDo(print())
            .andDo(document(documentIdentifier,
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(
                    parameterWithName("productId").description("상품 ID")
                ),
                requestFields(
                    fieldWithPath("content").type(JsonFieldType.STRING).description("리뷰 내용"),
                    fieldWithPath("rating").type(JsonFieldType.NUMBER).description("별점"),
                    fieldWithPath("imageUrls").type(JsonFieldType.ARRAY).description("리뷰 이미지 URL").optional()
                )
            ));
    }

    @Test
    @DisplayName("리뷰 작성시 해당 사용자가 해당 상품에 이미 리뷰를 작성했다면 HTTP 409을 응답한다.")
    void respond_409_when_user_requests_duplicate_product_review() throws Exception {
        CreateReviewRequestDto requestDto = CreateReviewRequestDto.builder()
            .rating(5)
            .content("맛있어요")
            .imageUrls(List.of("이미지 URL 1", "이미지 URL 2"))
            .build();

        willThrow(DUPLICATE_REVIEW).given(reviewService).createReview(any(), anyLong(), any());

        mockMvc.perform(post(PRODUCT_REVIEW_API_PATH, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isConflict())
            .andExpect(content().string(containsString(ErrorCode.DUPLICATE_REVIEW.getMessage())))
            .andDo(print());
    }

    @Test
    @DisplayName("리뷰 조회에 성공하면 HTTP 200을 응답한다.")
    void respond_200_when_success_to_read_reviews() throws Exception {
        ReadReviewRequestDto requestDto = new ReadReviewRequestDto(ReviewSortBy.LIKE,
            List.of(1L, 2L, 3L), List.of(2L), List.of(4, 5));

        List<ReviewDto> reviews = List.of(responseDto1, responseDto2);

        given(reviewService.readReviewList(any(), any(), any()))
            .willReturn(ReadReviewResponseDto.of(17, reviews));

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
                    fieldWithPath("data.latestReviewCount").type(JsonFieldType.NUMBER).description("최근 7일간 작성된 리뷰 개수"),
                    fieldWithPath("data.reviews[].productId").type(JsonFieldType.NUMBER).description("상품 ID"),
                    fieldWithPath("data.reviews[].productName").type(JsonFieldType.STRING).description("상품명"),
                    fieldWithPath("data.reviews[].productManufacturer").type(JsonFieldType.STRING).description("상품 제조사"),
                    fieldWithPath("data.reviews[].productImageUrl").type(JsonFieldType.STRING).description("상품 이미지 URL"),
                    fieldWithPath("data.reviews[].reviewId").type(JsonFieldType.NUMBER).description("리뷰 ID"),
                    fieldWithPath("data.reviews[].reviewerId").type(JsonFieldType.NUMBER).description("리뷰 작성자 ID"),
                    fieldWithPath("data.reviews[].reviewerNickname").type(JsonFieldType.STRING).description("리뷰 작성자 닉네임"),
                    fieldWithPath("data.reviews[].reviewerProfileImageUrl").type(JsonFieldType.STRING).description("리뷰 작성자 프로필 이미지 URL"),
                    fieldWithPath("data.reviews[].reviewerTags").type(JsonFieldType.ARRAY).description("리뷰 작성자의 태그 목록"),
                    fieldWithPath("data.reviews[].reviewLikeCount").type(JsonFieldType.NUMBER).description("리뷰 좋아요 개수"),
                    fieldWithPath("data.reviews[].reviewRating").type(JsonFieldType.NUMBER).description("리뷰 별점"),
                    fieldWithPath("data.reviews[].reviewContent").type(JsonFieldType.STRING).description("리뷰 내용"),
                    fieldWithPath("data.reviews[].isReviewLiked").type(JsonFieldType.BOOLEAN).description("사용자의 리뷰 좋아요 여부"),
                    fieldWithPath("data.reviews[].isProductBookmarked").type(JsonFieldType.BOOLEAN).description("사용자의 상품 북마크 여부"),
                    fieldWithPath("data.reviews[].reviewImageUrls").type(JsonFieldType.ARRAY).description("리뷰 이미지 URL 목록").optional(),
                    fieldWithPath("data.reviews[].createdAt").type(JsonFieldType.STRING).description("리뷰 생성 시간")
                )
            ));
    }

    @Test
    @DisplayName("특정 상품의 리뷰 목록 조회에 성공하면 HTTP 200을 응답한다.")
    void respond_200_when_success_to_read_product_reviews() throws Exception {
        ReadProductReviewRequestDto requestDto = new ReadProductReviewRequestDto(List.of(1L, 2L, 3L),
            List.of(4, 5), ReviewSortBy.LATEST);
        User reviewer = User.builder().id(1L).userId("abc@naver.com").role(Role.REGULAR)
            .nickname("닉네임").build();
        Review review = Review.builder().id(1L).rating(4).content("맛있어요").user(reviewer)
            .imageUrls(List.of()).build();
        ReadProductReviewQueryDto readProductReviewQueryDto = new ReadProductReviewQueryDto(reviewer.getId(),
            review.getId(), reviewer.getNickname(), reviewer.getProfileImageUrl(), null,
            review.getContent(), review.getRating(), null, review.getLikeCount(),
            LocalDateTime.now());
        ReadProductReviewResponseDto responseDto = ReadProductReviewResponseDto.of(readProductReviewQueryDto, reviewer,
            List.of("리뷰 이미지 URL", "리뷰 이미지 URL 2"), List.of("맵부심", "초코러버", "소식가"));

        given(reviewService.readProductReviewList(any(), anyLong(), any(), any()))
            .willReturn(new PageImpl<>(List.of(responseDto)));

        mockMvc.perform(get(PRODUCT_REVIEW_API_PATH, 1)
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andDo(print())
            .andDo(document(documentIdentifier,
                getDocumentRequest(),
                getDocumentResponse(),
                requestFields(
                    fieldWithPath("sortBy").type(JsonFieldType.STRING).description("정렬 기준").optional(),
                    fieldWithPath("tagIds").type(JsonFieldType.ARRAY).description("태그 ID 목록").optional(),
                    fieldWithPath("ratings").type(JsonFieldType.ARRAY).description("별점 목록").optional()
                ),
                relaxedResponseFields(
                    fieldWithPath("data.content[].reviewId").type(JsonFieldType.NUMBER).description("리뷰 ID"),
                    fieldWithPath("data.content[].reviewerId").type(JsonFieldType.NUMBER).description("리뷰 작성자 ID"),
                    fieldWithPath("data.content[].reviewerNickname").type(JsonFieldType.STRING).description("리뷰 작성자 닉네임"),
                    fieldWithPath("data.content[].reviewerProfileImageUrl").type(JsonFieldType.STRING).description("리뷰 작성자 프로필 이미지 URL").optional(),
                    fieldWithPath("data.content[].reviewerTags").type(JsonFieldType.ARRAY).description("리뷰 작성자의 태그 목록"),
                    fieldWithPath("data.content[].reviewLikeCount").type(JsonFieldType.NUMBER).description("리뷰 좋아요 개수"),
                    fieldWithPath("data.content[].reviewRating").type(JsonFieldType.NUMBER).description("리뷰 별점"),
                    fieldWithPath("data.content[].reviewContent").type(JsonFieldType.STRING).description("리뷰 내용"),
                    fieldWithPath("data.content[].isReviewLiked").type(JsonFieldType.BOOLEAN).description("사용자의 리뷰 좋아요 여부"),
                    fieldWithPath("data.content[].isFollowingUser").type(JsonFieldType.BOOLEAN).description("사용자가 리뷰 작성자를 팔로우하는지 여부"),
                    fieldWithPath("data.content[].isMe").type(JsonFieldType.BOOLEAN).description("로그인한 사용자가 리뷰 작성자인지 여부"),
                    fieldWithPath("data.content[].reviewImages").type(JsonFieldType.ARRAY).description("리뷰 이미지 URL 목록"),
                    fieldWithPath("data.content[].createdAt").type(JsonFieldType.STRING).description("리뷰 생성 시간").optional()
                )
            ));
    }

    @Test
    @DisplayName("권한이 없는 사용자가 특정 상품 리뷰를 조회하면 HTTP 403을 응답한다.")
    void respond_403_when_associate_member_try_to_read_product_reviews() throws Exception {

        given(reviewService.readProductReviewList(any(), anyLong(), any(), any()))
            .willThrow(FORBIDDEN_REVIEW);

        mockMvc.perform(get(PRODUCT_REVIEW_API_PATH, 1)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden()).andDo(print());
    }

    @Test
    @DisplayName("리뷰 수정에 성공하면 HTTP 200을 응답한다.")
    void respond_200_when_succeed_to_update_review() throws Exception {
        UpdateReviewRequestDto requestDto = new UpdateReviewRequestDto(5, "맛있어요",
            List.of("리뷰 이미지 URL 1", "리뷰 이미지 URL 2"));

        mockMvc.perform(put(UPDATE_REVIEW_API_PATH, 1)
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andDo(print())
            .andDo(document(documentIdentifier,
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(
                    parameterWithName("reviewId").description("리뷰 ID")
                ),
                requestFields(
                    fieldWithPath("content").type(JsonFieldType.STRING).description("리뷰 내용"),
                    fieldWithPath("rating").type(JsonFieldType.NUMBER).description("별점"),
                    fieldWithPath("imageUrls").type(JsonFieldType.ARRAY).description("리뷰 이미지 URL").optional()
                )
            ));
    }

    @ParameterizedTest
    @DisplayName("리뷰 작성시 별점이 1점 이상 5점 이하가 아니면 HTTP 400을 응답한다.")
    @ValueSource(ints = {0, 6, 7, 8, 9, 10, 100})
    void respond_400_when_rating_is_not_range_1_to_5(Integer rating) throws Exception {
        UpdateReviewRequestDto requestDto = new UpdateReviewRequestDto(rating, "맛있어요",
            List.of());

        mockMvc.perform(multipart(PRODUCT_REVIEW_API_PATH, 1)
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("평점은 1점 이상 5점 이하여야 합니다")))
            .andDo(print());
    }

    @Test
    @DisplayName("리뷰 작성시 리뷰 내용이 없으면 HTTP 400을 응답한다.")
    void respond_400_when_review_content_is_empty() throws Exception {
        CreateReviewRequestDto requestDto = CreateReviewRequestDto.builder()
            .rating(5)
            .content("")
            .imageUrls(List.of("이미지 URL 1", "이미지 URL 2"))
            .build();

        mockMvc.perform(multipart(PRODUCT_REVIEW_API_PATH, 1)
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("리뷰는 1자 이상 1000자 이하여야 합니다.")))
            .andDo(print());

    }

    @Test
    @DisplayName("리뷰 작성시 리뷰 내용이 1000글자를 초과하면 HTTP 400을 응답한다.")
    void respond_400_when_review_content_exceed_1000_letters() throws Exception {
        CreateReviewRequestDto requestDto = CreateReviewRequestDto.builder()
            .rating(5)
            .content("내용".repeat(501))
            .imageUrls(List.of("이미지 URL 1", "이미지 URL 2"))
            .build();

        mockMvc.perform(multipart(PRODUCT_REVIEW_API_PATH, 1)
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("리뷰는 1자 이상 1000자 이하여야 합니다.")))
            .andDo(print());
    }

    @Test
    @DisplayName("리뷰 작성시 리뷰 내용이 1000글자이면 HTTP 201을 응답한다.")
    void respond_201_when_review_content_does_not_exceed_1000_letters() throws Exception {
        CreateReviewRequestDto requestDto = CreateReviewRequestDto.builder()
            .rating(5)
            .content("내용".repeat(500))
            .imageUrls(List.of("이미지 URL 1", "이미지 URL 2"))
            .build();

        mockMvc.perform(multipart(PRODUCT_REVIEW_API_PATH, 1)
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated()).andDo(print());
    }

    @Test
    @DisplayName("리뷰 좋아요에 성공하면 HTTP 201을 응답한다")
    void respond_201_when_succeed_to_create_review_like() throws Exception {
        mockMvc.perform(post(REVIEW_LIKE_API_PATH, 1)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andDo(print())
            .andDo(document(documentIdentifier,
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(
                    parameterWithName("reviewId").description("리뷰 ID")
                )
            ));
    }

    @Test
    @DisplayName("리뷰 좋아요 요청시 해당 리뷰가 존재하지 않으면 HTTP 404를 응답한다")
    void respond_404_when_create_review_like_but_review_does_not_exist() throws Exception {
        willThrow(NOT_FOUND_REVIEW).given(reviewService).createReviewLike(any(), anyLong());

        mockMvc.perform(MockMvcRequestBuilders.post(REVIEW_LIKE_API_PATH, 1L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andDo(print());
    }

    @Test
    @DisplayName("리뷰 좋아요 요청시 사용자가 해당 리뷰에 이미 좋아요를 했으면 HTTP 409를 응답한다")
    void respond_409_when_create_review_like_but_review_like_already_exists() throws Exception {
        willThrow(DUPLICATE_REVIEW_LIKE).given(reviewService).createReviewLike(any(), anyLong());

        mockMvc.perform(MockMvcRequestBuilders.post(REVIEW_LIKE_API_PATH, 1L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict())
            .andDo(print());
    }

    @Test
    @DisplayName("리뷰 좋아요 처리 중 동시성 문제가 발생할 경우 HTTP 500을 응답한다")
    void respond_500_when_create_review_like_but_concurrency_issue_occurs() throws Exception {
        willThrow(ObjectOptimisticLockingFailureException.class)
            .given(reviewService).createReviewLike(any(), anyLong());

        mockMvc.perform(post(REVIEW_LIKE_API_PATH, 1L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError())
            .andDo(print());
    }

    @Test
    @DisplayName("리뷰 좋아요 취소에 성공하면 HTTP 200을 응답한다")
    void respond_200_when_succeed_to_delete_review_like() throws Exception {
        mockMvc.perform(delete(REVIEW_LIKE_API_PATH, 1)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document(documentIdentifier,
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(
                    parameterWithName("reviewId").description("리뷰 ID")
                )
            ));
    }

    @Test
    @DisplayName("리뷰 좋아요 취소 요청시 해당하는 리뷰가 없다면 HTTP 404를 응답한다")
    void respond_404_when_delete_review_like_but_review_does_not_exist() throws Exception {
        willThrow(NOT_FOUND_REVIEW).given(reviewService).deleteReviewLike(any(), anyLong());

        mockMvc.perform(delete(REVIEW_LIKE_API_PATH, 1L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andDo(print());
    }

    @Test
    @DisplayName("리뷰 좋아요 취소 요청시 해당하는 리뷰 좋아요가 없으면 HTTP 404를 응답한다")
    void respond_404_when_delete_review_like_but_review_like_does_not_exist() throws Exception {
        willThrow(NOT_FOUND_REVIEW_LIKE).given(reviewService).deleteReviewLike(any(), anyLong());

        mockMvc.perform(delete(REVIEW_LIKE_API_PATH, 1L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andDo(print());
    }

    @Test
    @DisplayName("리뷰 좋아요 취소 처리 중 동시성 문제가 발생할 경우 HTTP 500을 응답한다")
    void respond_500_when_delete_review_like_but_concurrency_issue_occurs() throws Exception {
        willThrow(ObjectOptimisticLockingFailureException.class)
            .given(reviewService).deleteReviewLike(any(), anyLong());

        mockMvc.perform(delete(REVIEW_LIKE_API_PATH, 1L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError())
            .andDo(print());
    }

    ReviewDto responseDto1 = ReviewDto.builder()
        .productId(13L)
        .productName("불닭볶음면큰컵")
        .productManufacturer("삼양")
        .productImageUrl("https://어쩌구저쩌구/product/불닭볶음면.jpg")
        .reviewId(1L)
        .reviewerId(3L)
        .reviewerNickname("불닭러버")
        .reviewerProfileImageUrl("https://어쩌구저쩌구/user/불닭러버.jpg")
        .reviewRating(5)
        .reviewContent("맛있어요")
        .reviewLikeCount(3L)
        .isProductBookmarked(false)
        .isReviewLiked(true)
        .reviewerTags(List.of("맵부심", "초코러버", "소식가"))
        .createdAt(LocalDateTime.now())
        .build();

    ReviewDto responseDto2 = ReviewDto.builder()
        .productId(13L)
        .productName("바질크림불닭우동")
        .productManufacturer("삼양")
        .productImageUrl("https://어쩌구저쩌구/product/바질크림불닭우동.jpg")
        .reviewId(2L)
        .reviewerId(3L)
        .reviewerNickname("불닭러버")
        .reviewerProfileImageUrl("https://어쩌구저쩌구/user/불닭러버.jpg")
        .reviewRating(5)
        .reviewContent("이번에 신제품 출시되었다고 해서 출시되자마자 먹어봤는데 생각보다 훨씬 맛있었어요")
        .reviewImageUrls(List.of("https://어쩌구저쩌구/review/신제품너무맛있다.jpg"))
        .reviewLikeCount(1L)
        .isProductBookmarked(true)
        .isReviewLiked(false)
        .reviewerTags(List.of("맵부심", "초코러버", "소식가"))
        .createdAt(LocalDateTime.now())
        .build();

}
