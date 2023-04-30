package com.cvsgo.service;

import static com.cvsgo.exception.ExceptionConstants.DUPLICATE_REVIEW;
import static com.cvsgo.exception.ExceptionConstants.FORBIDDEN_REVIEW;
import static com.cvsgo.exception.ExceptionConstants.NOT_FOUND_PRODUCT;
import static com.cvsgo.exception.ExceptionConstants.NOT_FOUND_REVIEW;
import static com.cvsgo.util.FileConstants.REVIEW_DIR_NAME;

import com.cvsgo.dto.review.CreateReviewRequestDto;
import com.cvsgo.dto.review.ReadProductReviewQueryDto;
import com.cvsgo.dto.review.ReadProductReviewRequestDto;
import com.cvsgo.dto.review.ReadProductReviewResponseDto;
import com.cvsgo.dto.review.ReadReviewQueryDto;
import com.cvsgo.dto.review.ReadReviewRequestDto;
import com.cvsgo.dto.review.ReadReviewResponseDto;
import com.cvsgo.dto.review.UpdateReviewRequestDto;
import com.cvsgo.entity.Product;
import com.cvsgo.entity.Review;
import com.cvsgo.entity.ReviewImage;
import com.cvsgo.entity.Role;
import com.cvsgo.entity.User;
import com.cvsgo.entity.UserTag;
import com.cvsgo.exception.DuplicateException;
import com.cvsgo.exception.ForbiddenException;
import com.cvsgo.exception.NotFoundException;
import com.cvsgo.exception.UnauthorizedException;
import com.cvsgo.repository.ProductRepository;
import com.cvsgo.repository.ReviewImageRepository;
import com.cvsgo.repository.ReviewRepository;
import com.cvsgo.repository.UserTagRepository;
import com.cvsgo.util.FileConstants;
import jakarta.persistence.EntityManager;
import java.io.IOException;
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

    private final FileUploadService fileUploadService;

    private final ReviewRepository reviewRepository;

    private final ProductRepository productRepository;

    private final UserTagRepository userTagRepository;

    private final ReviewImageRepository reviewImageRepository;

    private final EntityManager entityManager;

    /**
     * 리뷰를 추가합니다.
     *
     * @param user      리뷰를 작성한 사용자
     * @param productId 상품 ID
     * @param request   사용자가 작성한 리뷰 정보
     * @throws NotFoundException  해당 상품이 존재하지 않는 경우
     * @throws DuplicateException 해당 상품에 대한 리뷰를 이미 작성한 경우
     * @throws IOException        파일 접근에 실패한 경우
     */
    @Transactional
    public void createReview(User user, Long productId, CreateReviewRequestDto request)
        throws IOException {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> NOT_FOUND_PRODUCT);

        List<String> imageUrls = fileUploadService.upload(request.getImages(),
            FileConstants.REVIEW_DIR_NAME);

        Review review = request.toEntity(user, product, imageUrls);

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
     * @throws IOException           파일 접근에 실패한 경우
     */
    @Transactional
    public void updateReview(User user, Long reviewId, UpdateReviewRequestDto request)
        throws IOException {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> NOT_FOUND_REVIEW);

        if (!user.equals(review.getUser())) {
            throw FORBIDDEN_REVIEW;
        }

        List<String> imageUrls = fileUploadService.upload(request.getImages(), REVIEW_DIR_NAME);

        review.updateReviewImages(imageUrls);
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
    public List<ReadReviewResponseDto> getReviewList(User user, ReadReviewRequestDto request,
        Pageable pageable) {
        List<ReadReviewQueryDto> reviews = reviewRepository.findAllByFilter(user, request,
            pageable);

        List<UserTag> userTags = userTagRepository.findByUserIn(
            reviews.stream().map(ReadReviewQueryDto::getReviewer).distinct().toList());

        Map<User, List<UserTag>> userTagsByUser =
            userTags.stream().collect(Collectors.groupingBy(UserTag::getUser));

        List<ReviewImage> reviewImages = reviewImageRepository.findByReviewIdIn(
            reviews.stream().map(ReadReviewQueryDto::getReviewId).toList());

        Map<Long, List<ReviewImage>> reviewImagesByReview =
            reviewImages.stream()
                .collect(Collectors.groupingBy(reviewImage -> reviewImage.getReview().getId()));

        return reviews.stream()
            .map(reviewDto -> ReadReviewResponseDto.of(reviewDto,
                reviewImagesByReview.get(reviewDto.getReviewId()) != null
                    ? reviewImagesByReview.get(reviewDto.getReviewId()).stream()
                    .map(ReviewImage::getImageUrl).toList() : null,
                userTagsByUser.get(reviewDto.getReviewer()) != null
                    ? userTagsByUser.get(reviewDto.getReviewer()).stream()
                    .map(userTag -> userTag.getTag().getName()).toList() : null)
            ).toList();
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

        Map<Long, List<ReviewImage>> reviewImagesByReview = reviewImageRepository.findByReviewIdIn(
                reviews.stream().map(ReadProductReviewQueryDto::getReviewId).distinct().toList()).stream()
            .collect(Collectors.groupingBy(reviewImage -> reviewImage.getReview().getId()));

        Map<Long, List<UserTag>> userTagsByUser = userTagRepository.findByUserIdIn(
                reviews.stream().map(ReadProductReviewQueryDto::getReviewerId).distinct().toList()).stream()
            .collect(Collectors.groupingBy(userTag -> userTag.getUser().getId()));

        List<ReadProductReviewResponseDto> results = reviews.stream().map(
            reviewDto -> ReadProductReviewResponseDto.of(reviewDto, user,
                reviewImagesByReview.get(reviewDto.getReviewId()),
                userTagsByUser.get(reviewDto.getReviewerId()))).toList();

        return new PageImpl<>(results, pageable, totalCount);
    }

}
