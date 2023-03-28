package com.cvsgo.service;

import com.cvsgo.dto.review.CreateReviewRequestDto;
import com.cvsgo.dto.review.SearchReviewQueryDto;
import com.cvsgo.dto.review.SearchReviewRequestDto;
import com.cvsgo.entity.Product;
import com.cvsgo.entity.Review;
import com.cvsgo.entity.ReviewImage;
import com.cvsgo.entity.Tag;
import com.cvsgo.entity.User;
import com.cvsgo.entity.UserTag;
import com.cvsgo.exception.product.NotFoundProductException;
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

        reviewService.createReview(user, 1L, requestDto);

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

        reviewService.getReviewList(user, searchReviewRequest, PageRequest.of(0, 20));

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
                () -> reviewService.createReview(user, 100L, requestDto));
    }

    User user = User.builder().build();

    Product product = Product.builder().build();

    CreateReviewRequestDto requestDto = CreateReviewRequestDto.builder().build();

    SearchReviewRequestDto searchReviewRequest = SearchReviewRequestDto.builder().build();

    SearchReviewQueryDto searchReviewQueryDto = SearchReviewQueryDto.builder()
        .reviewId(1L)
        .productId(2L)
        .productName("불닭볶음면큰컵")
        .manufacturerName("삼양")
        .productImageUrl("https://어쩌구저쩌구/products/불닭볶음면.png")
        .reviewer(user)
        .likeCount(3L)
        .rating(4)
        .build();

    Tag tag = Tag.builder()
        .name("초코러버")
        .build();

    UserTag userTag = UserTag.builder()
        .user(user)
        .tag(tag)
        .build();

    ReviewImage reviewImage = ReviewImage.builder()
        .review(Review.builder().id(1L).imageUrls(new ArrayList<>()).build())
        .imageUrl("https://어쩌구저쩌구/review/리뷰이미지.png")
        .build();

}
