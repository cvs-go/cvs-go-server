package com.cvsgo.dto.review;

import com.cvsgo.entity.Review;
import com.cvsgo.entity.User;
import com.cvsgo.entity.UserFollow;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ReadReviewQueryDto {

    private final long reviewerId;

    private final long reviewId;

    private final String reviewerNickname;

    private final String reviewerProfileImageUrl;

    private final boolean isFollowing;

    private final String content;

    private final int rating;

    private final long likeCount;

    private final LocalDateTime createdAt;

    @QueryProjection
    public ReadReviewQueryDto(User reviewer, UserFollow userFollow, Review review) {
        this.reviewerId = reviewer.getId();
        this.reviewId = review.getId();
        this.reviewerNickname = reviewer.getNickname();
        this.reviewerProfileImageUrl = reviewer.getProfileImageUrl();
        this.isFollowing = userFollow != null;
        this.content = review.getContent();
        this.rating = review.getRating();
        this.likeCount = review.getLikeCount();
        this.createdAt = review.getCreatedAt();
    }
}
