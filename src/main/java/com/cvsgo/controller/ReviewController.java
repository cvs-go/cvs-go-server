package com.cvsgo.controller;

import com.cvsgo.argumentresolver.LoginUser;
import com.cvsgo.dto.SuccessResponse;
import com.cvsgo.dto.review.CreateReviewRequestDto;
import com.cvsgo.dto.review.ReadProductReviewRequestDto;
import com.cvsgo.dto.review.ReadProductReviewResponseDto;
import com.cvsgo.dto.review.ReadReviewRequestDto;
import com.cvsgo.dto.review.ReadReviewResponseDto;
import com.cvsgo.dto.review.UpdateReviewRequestDto;
import com.cvsgo.entity.User;
import com.cvsgo.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/products/{productId}/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public SuccessResponse<Void> createReview(@LoginUser User user, @PathVariable Long productId,
        @RequestBody @Valid CreateReviewRequestDto request) {
        reviewService.createReview(user, productId, request);
        return SuccessResponse.create();
    }

    @GetMapping("/reviews")
    public SuccessResponse<ReadReviewResponseDto> readReviews(@LoginUser User user,
        @ModelAttribute ReadReviewRequestDto request, Pageable pageable) {
        return SuccessResponse.from(reviewService.readReviewList(user, request, pageable));
    }

    @GetMapping("/products/{productId}/reviews")
    public SuccessResponse<Page<ReadProductReviewResponseDto>> readProductReviews(@LoginUser User user,
        @PathVariable Long productId, @ModelAttribute ReadProductReviewRequestDto request,
        Pageable pageable) {
        return SuccessResponse.from(
            reviewService.readProductReviewList(user, productId, request, pageable));
    }

    @PutMapping("/reviews/{reviewId}")
    public SuccessResponse<Void> updateReview(@LoginUser User user, @PathVariable Long reviewId,
        @RequestBody UpdateReviewRequestDto request) {
        reviewService.updateReview(user, reviewId, request);
        return SuccessResponse.create();
    }

    @DeleteMapping("/reviews/{reviewId}")
    public SuccessResponse<Void> deleteReview(@LoginUser User user, @PathVariable Long reviewId) {
        reviewService.deleteReview(user, reviewId);
        return SuccessResponse.create();
    }

    @PostMapping("/reviews/{reviewId}/likes")
    @ResponseStatus(HttpStatus.CREATED)
    public SuccessResponse<Void> createReviewLike(@LoginUser User user,
        @PathVariable Long reviewId) {
        reviewService.createReviewLike(user, reviewId);
        return SuccessResponse.create();
    }

    @DeleteMapping("/reviews/{reviewId}/likes")
    public SuccessResponse<Void> deleteReviewLike(@LoginUser User user,
        @PathVariable Long reviewId) {
        reviewService.deleteReviewLike(user, reviewId);
        return SuccessResponse.create();
    }

}
