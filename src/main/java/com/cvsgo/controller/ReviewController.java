package com.cvsgo.controller;

import com.cvsgo.argumentresolver.LoginUser;
import com.cvsgo.dto.SuccessResponse;
import com.cvsgo.dto.review.CreateReviewRequestDto;
import com.cvsgo.dto.review.ReadReviewRequestDto;
import com.cvsgo.dto.review.ReadReviewResponseDto;
import com.cvsgo.dto.review.SearchReviewRequestDto;
import com.cvsgo.dto.review.SearchReviewResponseDto;
import com.cvsgo.dto.review.UpdateReviewRequestDto;
import com.cvsgo.entity.User;
import com.cvsgo.service.ReviewService;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/products/{productId}/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public SuccessResponse<Void> createReview(@LoginUser User user, @PathVariable Long productId,
        @ModelAttribute @Valid CreateReviewRequestDto request) throws IOException {
        reviewService.createReview(user, productId, request);
        return SuccessResponse.create();
    }

    @GetMapping("/reviews")
    public SuccessResponse<List<SearchReviewResponseDto>> searchReviews(@LoginUser User user,
        @ModelAttribute SearchReviewRequestDto request, Pageable pageable) {
        return SuccessResponse.from(reviewService.getReviewList(user, request, pageable));
    }

    @GetMapping("/products/{productId}/reviews")
    public SuccessResponse<Page<ReadReviewResponseDto>> readReviews(@LoginUser User user,
        @PathVariable Long productId, @ModelAttribute ReadReviewRequestDto request,
        Pageable pageable) {
        return SuccessResponse.from(
            reviewService.readProductReviewList(user, productId, request, pageable));
    }

    @PutMapping("/reviews/{reviewId}")
    public SuccessResponse<Void> updateReview(@LoginUser User user, @PathVariable Long reviewId,
        @ModelAttribute UpdateReviewRequestDto request) throws IOException {
        reviewService.updateReview(user, reviewId, request);
        return SuccessResponse.create();
    }

}
