package com.cvsgo.service;

import com.cvsgo.dto.review.CreateReviewRequestDto;
import com.cvsgo.entity.Product;
import com.cvsgo.entity.Review;
import com.cvsgo.entity.User;
import com.cvsgo.repository.ProductRepository;
import com.cvsgo.repository.ReviewRepository;
import com.cvsgo.util.FileConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

import static com.cvsgo.exception.ExceptionConstants.NOT_FOUND_PRODUCT;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final S3FileUploadService fileUploadService;

    private final ReviewRepository reviewRepository;

    private final ProductRepository productRepository;

    /**
     * 리뷰를 추가합니다.
     * @param user 리뷰를 작성한 사용자
     * @param productId 상품 ID
     * @param request 사용자가 작성한 리뷰 정보
     * @throws IOException 파일 접근에 실패한 경우
     */
    @Transactional
    public void createReview(User user, Long productId, CreateReviewRequestDto request) throws IOException {
        Product product = productRepository.findById(productId).orElseThrow(() -> NOT_FOUND_PRODUCT);

        List<String> imageUrls = fileUploadService.upload(request.getImages(), FileConstants.REVIEW_DIR_NAME);

        Review review = Review.builder()
                .user(user)
                .product(product)
                .rating(request.getRating())
                .content(request.getContent())
                .imageUrls(imageUrls)
                .build();
        reviewRepository.save(review);
    }

}