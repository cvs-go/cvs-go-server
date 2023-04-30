package com.cvsgo.dto.review;

import com.cvsgo.entity.ReviewLike;
import com.cvsgo.entity.UserFollow;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ReadProductReviewQueryDto {

    private final long reviewerId;

    private final long reviewId;

    private final String reviewerNickname;

    private final String reviewerProfileImageUrl;

    private final boolean isFollowing;

    private final String content;

    private final int rating;

    private final boolean isReviewLiked;

    private final long likeCount;

    private final LocalDateTime createdAt;

    @QueryProjection
    public ReadProductReviewQueryDto(long reviewerId, long reviewId, String reviewerNickname,
        String reviewerProfileImageUrl, UserFollow userFollow, String content, int rating,
        ReviewLike reviewLike, long likeCount, LocalDateTime createdAt) {
        this.reviewerId = reviewerId;
        this.reviewId = reviewId;
        this.reviewerNickname = reviewerNickname;
        this.reviewerProfileImageUrl = reviewerProfileImageUrl;
        this.isFollowing = userFollow != null;
        this.content = content;
        this.rating = rating;
        this.isReviewLiked = reviewLike != null;
        this.likeCount = likeCount;
        this.createdAt = createdAt;
    }
}
