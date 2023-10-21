package com.cvsgo.dto.review;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReadUserReviewResponseDto {

    private final Long productId;

    private final String productName;

    private final String productManufacturer;

    private final String productImageUrl;

    private final Long reviewId;

    private final Long reviewerId;

    private final String reviewerNickname;

    private final String reviewerProfileImageUrl;

    private final Long reviewLikeCount;

    private final Integer reviewRating;

    private final String reviewContent;

    private final Boolean isReviewLiked;

    private final Boolean isProductBookmarked;

    private final List<String> reviewImageUrls;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime createdAt;

    @Builder
    public ReadUserReviewResponseDto(Long productId, String productName, String productManufacturer,
        String productImageUrl, Long reviewId, Long reviewerId, String reviewerNickname,
        String reviewerProfileImageUrl,
        Long reviewLikeCount, Integer reviewRating, String reviewContent, Boolean isReviewLiked,
        Boolean isProductBookmarked, List<String> reviewImageUrls, LocalDateTime createdAt) {
        this.productId = productId;
        this.productName = productName;
        this.productManufacturer = productManufacturer;
        this.productImageUrl = productImageUrl;
        this.reviewId = reviewId;
        this.reviewerId = reviewerId;
        this.reviewerNickname = reviewerNickname;
        this.reviewerProfileImageUrl = reviewerProfileImageUrl;
        this.reviewLikeCount = reviewLikeCount;
        this.reviewRating = reviewRating;
        this.reviewContent = reviewContent;
        this.isReviewLiked = isReviewLiked;
        this.isProductBookmarked = isProductBookmarked;
        this.reviewImageUrls = reviewImageUrls;
        this.createdAt = createdAt;
    }

    public static ReadUserReviewResponseDto of(ReadUserReviewQueryDto readUserReviewQueryDto,
        List<String> reviewImageUrls) {
        return ReadUserReviewResponseDto.builder()
            .productName(readUserReviewQueryDto.getProductName())
            .productId(readUserReviewQueryDto.getProductId())
            .reviewContent(readUserReviewQueryDto.getReviewContent())
            .productImageUrl(readUserReviewQueryDto.getProductImageUrl())
            .productManufacturer(readUserReviewQueryDto.getManufacturerName())
            .reviewId(readUserReviewQueryDto.getReviewId())
            .reviewerId(readUserReviewQueryDto.getReviewerId())
            .reviewerNickname(readUserReviewQueryDto.getReviewerNickname())
            .reviewerProfileImageUrl(readUserReviewQueryDto.getReviewerProfileImageUrl())
            .reviewRating(readUserReviewQueryDto.getRating())
            .reviewLikeCount(readUserReviewQueryDto.getLikeCount())
            .createdAt(readUserReviewQueryDto.getCreatedAt())
            .isProductBookmarked(readUserReviewQueryDto.getIsProductBookmarked())
            .isReviewLiked(readUserReviewQueryDto.getIsReviewLiked())
            .reviewImageUrls(reviewImageUrls)
            .build();
    }
}
