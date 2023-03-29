package com.cvsgo.dto.review;

import com.cvsgo.entity.ProductBookmark;
import com.cvsgo.entity.ReviewLike;
import com.cvsgo.entity.User;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SearchReviewQueryDto {

    private final Long reviewId;

    private final Long productId;

    private final String productName;

    private final String manufacturerName;

    private final String productImageUrl;

    private final User reviewer;

    private final Long likeCount;

    private final Integer rating;

    private final String reviewContent;

    private final LocalDateTime createdAt;

    private final Boolean isReviewLiked;

    private final Boolean isProductBookmarked;

    @Builder
    @QueryProjection
    public SearchReviewQueryDto(Long reviewId, Long productId, String productName,
        String manufacturerName, String productImageUrl, User reviewer, Long likeCount,
        Integer rating, String reviewContent, LocalDateTime createdAt, ReviewLike reviewLike,
        ProductBookmark productBookmark) {
        this.reviewId = reviewId;
        this.productId = productId;
        this.productName = productName;
        this.manufacturerName = manufacturerName;
        this.productImageUrl = productImageUrl;
        this.reviewer = reviewer;
        this.likeCount = likeCount;
        this.rating = rating;
        this.reviewContent = reviewContent;
        this.createdAt = createdAt;
        this.isReviewLiked = reviewLike != null;
        this.isProductBookmarked = productBookmark != null;
    }
}
