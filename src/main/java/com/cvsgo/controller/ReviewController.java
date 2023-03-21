package com.cvsgo.controller;

import com.cvsgo.argumentresolver.LoginUser;
import com.cvsgo.dto.SuccessResponse;
import com.cvsgo.dto.review.CreateReviewRequestDto;
import com.cvsgo.entity.User;
import com.cvsgo.service.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

}
