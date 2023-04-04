package com.cvsgo.service;

import com.cvsgo.dto.review.CreateReviewRequestDto;
import com.cvsgo.dto.review.SearchReviewQueryDto;
import com.cvsgo.dto.review.SearchReviewRequestDto;
import com.cvsgo.dto.review.UpdateReviewRequestDto;
import com.cvsgo.entity.Product;
import com.cvsgo.entity.Review;
import com.cvsgo.entity.ReviewImage;
import com.cvsgo.entity.Tag;
import com.cvsgo.entity.User;
import com.cvsgo.entity.UserTag;
import com.cvsgo.exception.auth.UnauthorizedUserException;
import com.cvsgo.exception.product.NotFoundProductException;
import com.cvsgo.exception.review.NotFoundReviewException;
import com.cvsgo.repository.ProductRepository;
import com.cvsgo.repository.ReviewImageRepository;
import com.cvsgo.repository.ReviewRepository;
import com.cvsgo.repository.UserTagRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private FileUploadService fileUploadService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserTagRepository userTagRepository;

    @Mock
    private ReviewImageRepository reviewImageRepository;

    @InjectMocks
    ReviewService reviewService;

    @Test
    @DisplayName("리뷰를 정상적으로 추가한다")
    void succeed_to_create_review() throws Exception {
        given(reviewRepository.save(any())).willReturn(any());
        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        reviewService.createReview(user1, 1L, createReviewRequestDto);

        then(productRepository).should(times(1)).findById(1L);
        then(reviewRepository).should(times(1)).save(any());
    }

    @Test
    @DisplayName("리뷰를 정상적으로 조회한다")
    void succeed_to_read_review() {
        given(reviewRepository.searchByFilter(any(), any(), any()))
            .willReturn(List.of(searchReviewQueryDto));
        given(userTagRepository.findByUserIn(anyList()))
            .willReturn(List.of(userTag));
        given(reviewImageRepository.findByReviewIdIn(anyList()))
            .willReturn(List.of(reviewImage));

        reviewService.getReviewList(user1, searchReviewRequest, PageRequest.of(0, 20));

        then(reviewRepository)
            .should(times(1)).searchByFilter(any(), any(), any());
        then(userTagRepository).should(times(1)).findByUserIn(any());
        then(reviewImageRepository).should(times(1)).findByReviewIdIn(any());
    }

    @Test
    @DisplayName("해당 ID의 상품이 없는 경우 NotFoundProductException이 발생한다")
    void should_throw_NotFoundProductException_when_product_does_not_exist() {
        given(productRepository.findById(anyLong()))
                .willThrow(NotFoundProductException.class);

        assertThrows(NotFoundProductException.class,
                () -> reviewService.createReview(user1, 100L, createReviewRequestDto));
    }

    @Test
    @DisplayName("리뷰 수정시 해당 ID의 리뷰가 없는 경우 NotFoundReviewException이 발생한다")
    void should_throw_NotFoundReviewException_when_review_does_not_exist() {
        given(reviewRepository.findById(anyLong()))
            .willThrow(NotFoundReviewException.class);

        assertThrows(NotFoundReviewException.class,
            () -> reviewService.updateReview(user1, 100L, updateReviewRequestDto));
    }

    @Test
    @DisplayName("해당 리뷰 작성자 본인이 아닌 사용자가 리뷰 수정을 시도할 경우 UnauthorizedUserException이 발생한다")
    void should_throw_UnauthorizedUserException_when_user_is_not_the_reviewer() {
        given(reviewRepository.findById(anyLong()))
            .willReturn(Optional.of(review));

        assertThrows(UnauthorizedUserException.class,
            () -> reviewService.updateReview(user1, 100L, updateReviewRequestDto));
    }

    User user1 = User.builder().build();

    User user2 = User.builder().id(2L).build();

    Product product = Product.builder().build();

    Review review = Review.builder().user(user2).imageUrls(new ArrayList<>()).build();

    CreateReviewRequestDto createReviewRequestDto = CreateReviewRequestDto.builder().build();

    UpdateReviewRequestDto updateReviewRequestDto = new UpdateReviewRequestDto(5, "맛있어요",
        new ArrayList<>());

    SearchReviewRequestDto searchReviewRequest = SearchReviewRequestDto.builder().build();

    SearchReviewQueryDto searchReviewQueryDto = SearchReviewQueryDto.builder()
        .reviewId(1L)
        .productId(2L)
        .productName("불닭볶음면큰컵")
        .manufacturerName("삼양")
        .productImageUrl("https://어쩌구저쩌구/products/불닭볶음면.png")
        .reviewer(user1)
        .likeCount(3L)
        .rating(4)
        .build();

    Tag tag = Tag.builder()
        .name("초코러버")
        .build();

    UserTag userTag = UserTag.builder()
        .user(user1)
        .tag(tag)
        .build();

    ReviewImage reviewImage = ReviewImage.builder()
        .review(Review.builder().id(1L).imageUrls(new ArrayList<>()).build())
        .imageUrl("https://어쩌구저쩌구/review/리뷰이미지.png")
        .build();

}
