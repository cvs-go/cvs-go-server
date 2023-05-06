package com.cvsgo.dto.review;

import com.cvsgo.entity.ProductBookmark;
import com.cvsgo.entity.ReviewLike;
import com.cvsgo.entity.UserFollow;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ReadReviewQueryDto {

    private final Long reviewId;

    private final Long productId;

    private final String productName;

    private final String manufacturerName;

    private final String productImageUrl;

    private final Long reviewerId;

    private final String reviewerNickname;

    private final String reviewerProfileImageUrl;

    private final boolean isFollowing;

    private final Long likeCount;

    private final Integer rating;

    private final String reviewContent;

    private final LocalDateTime createdAt;

    private final Boolean isReviewLiked;

    private final Boolean isProductBookmarked;

    @QueryProjection
    public ReadReviewQueryDto(Long reviewId, Long productId, String productName,
        String manufacturerName, String productImageUrl, Long reviewerId, String reviewerNickname,
        String reviewerProfileImageUrl, UserFollow userFollow, Long likeCount, Integer rating,
        String reviewContent, LocalDateTime createdAt, ReviewLike reviewLike,
        ProductBookmark productBookmark) {
        this.reviewId = reviewId;
        this.productId = productId;
        this.productName = productName;
        this.manufacturerName = manufacturerName;
        this.productImageUrl = productImageUrl;
        this.reviewerId = reviewerId;
        this.reviewerNickname = reviewerNickname;
        this.reviewerProfileImageUrl = reviewerProfileImageUrl;
        this.isFollowing = userFollow != null;
        this.likeCount = likeCount;
        this.rating = rating;
        this.reviewContent = reviewContent;
        this.createdAt = createdAt;
        this.isReviewLiked = reviewLike != null;
        this.isProductBookmarked = productBookmark != null;
    }
}
