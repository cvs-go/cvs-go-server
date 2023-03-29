package com.cvsgo.dto.review;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SearchReviewResponseDto {

    private final Long productId;

    private final String productName;

    private final String productManufacturer;

    private final String productImageUrl;

    private final Long reviewerId;

    private final String reviewerNickname;

    private final String reviewerProfileImageUrl;

    private final List<String> reviewerTags;

    private final Long reviewLikeCount;

    private final Integer rating;

    private final String reviewContent;

    private final Boolean isReviewLiked;

    private final Boolean isProductBookmarked;

    private final List<String> reviewImageUrls;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime createdAt;

    @Builder
    public SearchReviewResponseDto(Long productId, String productName, String productManufacturer,
        String productImageUrl, Long reviewerId, String reviewerNickname,
        String reviewerProfileImageUrl, List<String> reviewerTags,
        Long reviewLikeCount, Integer rating, String reviewContent, LocalDateTime createdAt,
        Boolean isReviewLiked, Boolean isProductBookmarked, List<String> reviewImageUrls) {
        this.productId = productId;
        this.productName = productName;
        this.productManufacturer = productManufacturer;
        this.productImageUrl = productImageUrl;
        this.reviewerId = reviewerId;
        this.reviewerNickname = reviewerNickname;
        this.reviewerProfileImageUrl = reviewerProfileImageUrl;
        this.reviewerTags = reviewerTags;
        this.reviewLikeCount = reviewLikeCount;
        this.rating = rating;
        this.reviewContent = reviewContent;
        this.createdAt = createdAt;
        this.isReviewLiked = isReviewLiked;
        this.isProductBookmarked = isProductBookmarked;
        this.reviewImageUrls = reviewImageUrls;
    }

    public static SearchReviewResponseDto of(SearchReviewQueryDto searchReviewQueryDto,
        List<String> reviewImageUrls, List<String> tags) {
        return SearchReviewResponseDto.builder()
            .productName(searchReviewQueryDto.getProductName())
            .productId(searchReviewQueryDto.getProductId())
            .reviewContent(searchReviewQueryDto.getReviewContent())
            .productImageUrl(searchReviewQueryDto.getProductImageUrl())
            .productManufacturer(searchReviewQueryDto.getManufacturerName())
            .reviewerId(searchReviewQueryDto.getReviewer().getId())
            .reviewerNickname(searchReviewQueryDto.getReviewer().getNickname())
            .reviewerProfileImageUrl(searchReviewQueryDto.getReviewer().getProfileImageUrl())
            .rating(searchReviewQueryDto.getRating())
            .reviewLikeCount(searchReviewQueryDto.getLikeCount())
            .createdAt(searchReviewQueryDto.getCreatedAt())
            .isProductBookmarked(searchReviewQueryDto.getIsProductBookmarked())
            .isReviewLiked(searchReviewQueryDto.getIsReviewLiked())
            .reviewImageUrls(reviewImageUrls)
            .reviewerTags(tags)
            .build();
    }
}
