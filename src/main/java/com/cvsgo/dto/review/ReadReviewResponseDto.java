package com.cvsgo.dto.review;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReadReviewResponseDto {

    private final Long productId;

    private final String productName;

    private final String productManufacturer;

    private final String productImageUrl;

    private final Long reviewId;

    private final Long reviewerId;

    private final String reviewerNickname;

    private final String reviewerProfileImageUrl;

    private final List<String> reviewerTags;

    private final Long reviewLikeCount;

    private final Integer reviewRating;

    private final String reviewContent;

    private final Boolean isReviewLiked;

    private final Boolean isProductBookmarked;

    private final List<String> reviewImageUrls;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime createdAt;

    @Builder
    public ReadReviewResponseDto(Long productId, String productName, String productManufacturer,
        String productImageUrl, Long reviewId, Long reviewerId, String reviewerNickname,
        String reviewerProfileImageUrl, List<String> reviewerTags,
        Long reviewLikeCount, Integer reviewRating, String reviewContent, LocalDateTime createdAt,
        Boolean isReviewLiked, Boolean isProductBookmarked, List<String> reviewImageUrls) {
        this.productId = productId;
        this.productName = productName;
        this.productManufacturer = productManufacturer;
        this.productImageUrl = productImageUrl;
        this.reviewId = reviewId;
        this.reviewerId = reviewerId;
        this.reviewerNickname = reviewerNickname;
        this.reviewerProfileImageUrl = reviewerProfileImageUrl;
        this.reviewerTags = reviewerTags;
        this.reviewLikeCount = reviewLikeCount;
        this.reviewRating = reviewRating;
        this.reviewContent = reviewContent;
        this.createdAt = createdAt;
        this.isReviewLiked = isReviewLiked;
        this.isProductBookmarked = isProductBookmarked;
        this.reviewImageUrls = reviewImageUrls;
    }

    public static ReadReviewResponseDto of(ReadReviewQueryDto readReviewQueryDto,
        List<String> reviewImageUrls, List<String> tags) {
        return ReadReviewResponseDto.builder()
            .productName(readReviewQueryDto.getProductName())
            .productId(readReviewQueryDto.getProductId())
            .reviewContent(readReviewQueryDto.getReviewContent())
            .productImageUrl(readReviewQueryDto.getProductImageUrl())
            .productManufacturer(readReviewQueryDto.getManufacturerName())
            .reviewId(readReviewQueryDto.getReviewId())
            .reviewerId(readReviewQueryDto.getReviewer().getId())
            .reviewerNickname(readReviewQueryDto.getReviewer().getNickname())
            .reviewerProfileImageUrl(readReviewQueryDto.getReviewer().getProfileImageUrl())
            .reviewRating(readReviewQueryDto.getRating())
            .reviewLikeCount(readReviewQueryDto.getLikeCount())
            .createdAt(readReviewQueryDto.getCreatedAt())
            .isProductBookmarked(readReviewQueryDto.getIsProductBookmarked())
            .isReviewLiked(readReviewQueryDto.getIsReviewLiked())
            .reviewImageUrls(reviewImageUrls)
            .reviewerTags(tags)
            .build();
    }
}
