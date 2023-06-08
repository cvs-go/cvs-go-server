package com.cvsgo.service;

import static com.cvsgo.exception.ExceptionConstants.DUPLICATE_REVIEW;
import static com.cvsgo.exception.ExceptionConstants.DUPLICATE_REVIEW_LIKE;
import static com.cvsgo.exception.ExceptionConstants.FORBIDDEN_REVIEW;
import static com.cvsgo.exception.ExceptionConstants.NOT_FOUND_PRODUCT;
import static com.cvsgo.exception.ExceptionConstants.NOT_FOUND_REVIEW;
import static com.cvsgo.exception.ExceptionConstants.NOT_FOUND_REVIEW_LIKE;

import com.cvsgo.dto.review.CreateReviewRequestDto;
import com.cvsgo.dto.review.ReadProductReviewQueryDto;
import com.cvsgo.dto.review.ReadProductReviewRequestDto;
import com.cvsgo.dto.review.ReadProductReviewResponseDto;
import com.cvsgo.dto.review.ReadReviewQueryDto;
import com.cvsgo.dto.review.ReadReviewRequestDto;
import com.cvsgo.dto.review.ReadReviewResponseDto;
import com.cvsgo.dto.review.ReviewDto;
import com.cvsgo.dto.review.UpdateReviewRequestDto;
import com.cvsgo.entity.Product;
import com.cvsgo.entity.Review;
import com.cvsgo.entity.ReviewImage;
import com.cvsgo.entity.ReviewLike;
import com.cvsgo.entity.Role;
import com.cvsgo.entity.User;
import com.cvsgo.entity.UserTag;
import com.cvsgo.exception.DuplicateException;
import com.cvsgo.exception.ForbiddenException;
import com.cvsgo.exception.NotFoundException;
import com.cvsgo.exception.UnauthorizedException;
import com.cvsgo.repository.ProductRepository;
import com.cvsgo.repository.ReviewImageRepository;
import com.cvsgo.repository.ReviewLikeRepository;
import com.cvsgo.repository.ReviewRepository;
import com.cvsgo.repository.UserTagRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final ProductRepository productRepository;

    private final UserTagRepository userTagRepository;

    private final ReviewImageRepository reviewImageRepository;

    private final ReviewLikeRepository reviewLikeRepository;

    private final EntityManager entityManager;

    /**
     * 리뷰를 추가합니다.
     *
     * @param user      리뷰를 작성한 사용자
     * @param productId 상품 ID
     * @param request   사용자가 작성한 리뷰 정보
     * @throws NotFoundException  해당 상품이 존재하지 않는 경우
     * @throws DuplicateException 해당 상품에 대한 리뷰를 이미 작성한 경우
     */
    @Transactional
    public void createReview(User user, Long productId, CreateReviewRequestDto request) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> NOT_FOUND_PRODUCT);

        Review review = request.toEntity(user, product);

        try {
            reviewRepository.save(review);
        } catch (DataIntegrityViolationException e) {
            entityManager.clear();
            if (reviewRepository.existsByProductAndUser(product, user)) {
                throw DUPLICATE_REVIEW;
            }
            throw e;
        }
        if (reviewRepository.countByUser(user) == 5) {
            user.updateRoleToRegular();
        }
    }

    /**
     * 리뷰를 수정합니다.
     *
     * @param user     현재 로그인한 사용자
     * @param reviewId 리뷰 ID
     * @param request  수정할 리뷰 정보
     * @throws UnauthorizedException 리뷰를 작성한 사용자가 아닌 경우
     */
    @Transactional
    public void updateReview(User user, Long reviewId, UpdateReviewRequestDto request) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> NOT_FOUND_REVIEW);

        if (!user.equals(review.getUser())) {
            throw FORBIDDEN_REVIEW;
        }

        review.updateReviewImages(request.getImageUrls());
        review.updateContent(request.getContent());
        review.updateRating(request.getRating());
    }

    /**
     * 필터를 적용하여 리뷰를 조회합니다.
     *
     * @param user     현재 로그인한 사용자
     * @param request  리뷰 필터 정보
     * @param pageable 페이지 정보
     * @return 리뷰 목록
     */
    @Transactional(readOnly = true)
    public ReadReviewResponseDto readReviewList(User user, ReadReviewRequestDto request,
        Pageable pageable) {
        List<ReadReviewQueryDto> reviews = reviewRepository.findAllByFilter(user, request,
            pageable);

        List<UserTag> userTags = userTagRepository.findByUserIdIn(
            reviews.stream().map(ReadReviewQueryDto::getReviewerId).distinct().toList());

        List<ReviewImage> reviewImages = reviewImageRepository.findByReviewIdIn(
            reviews.stream().map(ReadReviewQueryDto::getReviewId).toList());

        Long latestReviewCount = reviewRepository.countLatestReviews();

        List<ReviewDto> reviewDtos = reviews.stream().map(reviewDto -> ReviewDto.of(reviewDto,
            getReviewImages(reviewDto.getReviewId(), reviewImages),
            getUserTags(reviewDto.getReviewerId(), userTags))
        ).toList();

        return ReadReviewResponseDto.of(latestReviewCount, reviewDtos);
    }

    /**
     * 특정 상품의 리뷰 목록을 조회합니다.
     *
     * @param user      현재 로그인한 사용자
     * @param productId 상품 ID
     * @param request   필터 정보
     * @param pageable  페이지 정보
     * @return 리뷰 목록
     * @throws ForbiddenException 정회원이 아닌 회원이 0페이지가 아닌 다른 페이지를 조회하는 경우
     */
    @Transactional(readOnly = true)
    public Page<ReadProductReviewResponseDto> readProductReviewList(User user, Long productId,
        ReadProductReviewRequestDto request, Pageable pageable) {

        if (user == null || user.getRole() != Role.REGULAR) {
            if (pageable.getPageNumber() > 0) {
                throw FORBIDDEN_REVIEW;
            } else {
                pageable = PageRequest.of(pageable.getPageNumber(), 5);
            }
        }

        List<ReadProductReviewQueryDto> reviews = reviewRepository.findAllByProductIdAndFilter(user,
            productId, request, pageable);

        Long totalCount = reviewRepository.countByProductIdAndFilter(productId, request);

        List<ReviewImage> reviewImages = reviewImageRepository.findByReviewIdIn(
            reviews.stream().map(ReadProductReviewQueryDto::getReviewId).distinct().toList());

        List<UserTag> userTags = userTagRepository.findByUserIdIn(
            reviews.stream().map(ReadProductReviewQueryDto::getReviewerId).distinct().toList());

        List<ReadProductReviewResponseDto> results = reviews.stream().map(
            reviewDto -> ReadProductReviewResponseDto.of(reviewDto, user,
                getReviewImages(reviewDto.getReviewId(), reviewImages),
                getUserTags(reviewDto.getReviewerId(), userTags))).toList();

        return new PageImpl<>(results, pageable, totalCount);
    }

    /**
     * 리뷰 좋아요를 추가합니다.
     *
     * @param user     현재 로그인한 사용자
     * @param reviewId 좋아요 하려는 리뷰 ID
     * @throws NotFoundException  해당 리뷰가 존재하지 않는 경우
     * @throws DuplicateException 사용자가 해당 리뷰에 좋아요를 이미 한 경우
     */
    @Transactional
    public void createReviewLike(User user, Long reviewId) {
        Review review = reviewRepository.findByIdWithOptimisticLock(reviewId)
            .orElseThrow(() -> NOT_FOUND_REVIEW);

        if (reviewLikeRepository.existsByReviewAndUser(review, user)) {
            throw DUPLICATE_REVIEW_LIKE;
        }
        ReviewLike reviewLike = ReviewLike.create(user, review);
        reviewLikeRepository.save(reviewLike);
        review.plusLikeCount();
    }

    /**
     * 리뷰 좋아요를 삭제합니다.
     *
     * @param user     현재 로그인한 사용자
     * @param reviewId 좋아요 취소하려는 리뷰 ID
     * @throws NotFoundException 해당 리뷰가 존재하지 않거나 사용자가 해당 리뷰에 좋아요를 하지 않은 경우
     */
    @Transactional
    public void deleteReviewLike(User user, Long reviewId) {
        Review review = reviewRepository.findByIdWithOptimisticLock(reviewId)
            .orElseThrow(() -> NOT_FOUND_REVIEW);

        ReviewLike reviewLike = reviewLikeRepository.findByReviewAndUser(review, user)
            .orElseThrow(() -> NOT_FOUND_REVIEW_LIKE);
        reviewLikeRepository.delete(reviewLike);
        review.minusLikeCount();
    }

    private List<String> getReviewImages(Long reviewId, List<ReviewImage> reviewImages) {
        Map<Long, List<ReviewImage>> reviewImagesByReview = reviewImages.stream()
            .collect(Collectors.groupingBy(reviewImage -> reviewImage.getReview().getId()));

        return reviewImagesByReview.get(reviewId) != null
            ? reviewImagesByReview.get(reviewId).stream().map(ReviewImage::getImageUrl).toList()
            : List.of();
    }

    private List<String> getUserTags(Long reviewerId, List<UserTag> userTags) {
        Map<Long, List<UserTag>> userTagsByUser = userTags.stream()
            .collect(Collectors.groupingBy(userTag -> userTag.getUser().getId()));

        return userTagsByUser.get(reviewerId) != null
            ? userTagsByUser.get(reviewerId).stream().map(userTag -> userTag.getTag().getName())
            .toList() : List.of();
    }

}
