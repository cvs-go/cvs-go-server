package com.cvsgo.service;

import com.cvsgo.dto.review.CreateReviewRequestDto;
import com.cvsgo.entity.Product;
import com.cvsgo.entity.User;
import com.cvsgo.exception.product.NotFoundProductException;
import com.cvsgo.repository.ProductRepository;
import com.cvsgo.repository.ReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private FileUploadService fileUploadService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ProductRepository productRepository;

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

}
