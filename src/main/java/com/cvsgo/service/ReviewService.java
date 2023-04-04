package com.cvsgo.service;

import com.cvsgo.dto.review.CreateReviewRequestDto;
import com.cvsgo.dto.review.SearchReviewQueryDto;
import com.cvsgo.dto.review.SearchReviewRequestDto;
import com.cvsgo.dto.review.SearchReviewResponseDto;
import com.cvsgo.entity.Product;
import com.cvsgo.entity.Review;
import com.cvsgo.entity.ReviewImage;
import com.cvsgo.entity.User;
import com.cvsgo.entity.UserTag;
import com.cvsgo.repository.ProductRepository;
import com.cvsgo.repository.ReviewImageRepository;
import com.cvsgo.repository.ReviewRepository;
import com.cvsgo.repository.UserTagRepository;
import com.cvsgo.util.FileConstants;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cvsgo.exception.product.NotFoundProductException;

import java.io.IOException;
import java.util.List;

import static com.cvsgo.exception.ExceptionConstants.NOT_FOUND_PRODUCT;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final FileUploadService fileUploadService;

    private final ReviewRepository reviewRepository;

    private final ProductRepository productRepository;

    private final UserTagRepository userTagRepository;

    private final ReviewImageRepository reviewImageRepository;

    /**
     * 리뷰를 추가합니다.
     *
     * @param user      리뷰를 작성한 사용자
     * @param productId 상품 ID
     * @param request   사용자가 작성한 리뷰 정보
     * @throws NotFoundProductException 해당 상품이 존재하지 않는 경우
     * @throws IOException              파일 접근에 실패한 경우
     */
    @Transactional(rollbackFor = Exception.class)
    public void createReview(User user, Long productId, CreateReviewRequestDto request)
        throws IOException {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> NOT_FOUND_PRODUCT);

        List<String> imageUrls = fileUploadService.upload(request.getImages(),
            FileConstants.REVIEW_DIR_NAME);

        Review review = request.toEntity(user, product, imageUrls);
        reviewRepository.save(review);
    }

    /**
     * 필터를 적용하여 리뷰를 조회합니다.
     *
     * @param user     현재 로그인한 사용자
     * @param request  리뷰 필터 정보
     * @param pageable 페이지 정보
     * @return 리뷰 목록
     */
    public List<SearchReviewResponseDto> getReviewList(User user, SearchReviewRequestDto request,
        Pageable pageable) {
        List<SearchReviewQueryDto> reviews = reviewRepository.searchByFilter(user, request,
            pageable);

        List<UserTag> userTags = userTagRepository.findByUserIn(
            reviews.stream().map(SearchReviewQueryDto::getReviewer).distinct().toList());

        Map<User, List<UserTag>> userTagsByUser =
            userTags.stream().collect(Collectors.groupingBy(UserTag::getUser));

        List<ReviewImage> reviewImages = reviewImageRepository.findByReviewIdIn(
            reviews.stream().map(SearchReviewQueryDto::getReviewId).toList());

        Map<Long, List<ReviewImage>> reviewImagesByReview =
            reviewImages.stream()
                .collect(Collectors.groupingBy(reviewImage -> reviewImage.getReview().getId()));

        return reviews.stream()
            .map(reviewDto -> SearchReviewResponseDto.of(reviewDto,
                reviewImagesByReview.get(reviewDto.getReviewId()) != null
                    ? reviewImagesByReview.get(reviewDto.getReviewId()).stream()
                    .map(ReviewImage::getImageUrl).toList() : null,
                userTagsByUser.get(reviewDto.getReviewer()).stream()
                    .map(userTag -> userTag.getTag().getName()).toList())
            ).toList();
    }

}