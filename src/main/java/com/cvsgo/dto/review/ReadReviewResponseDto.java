package com.cvsgo.dto.review;

import com.cvsgo.entity.ReviewImage;
import com.cvsgo.entity.User;
import com.cvsgo.entity.UserTag;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReadReviewResponseDto {

    private final Long reviewId;

    private final Long reviewerId;

    private final String reviewerNickname;

    private final Boolean isFollowingUser;

    private final Boolean isMe;

    private final String reviewContent;

    private final Integer reviewRating;

    private final Long reviewLikeCount;

    private final List<String> reviewerTags;

    private final List<String> reviewImages;

    @Builder
    private ReadReviewResponseDto(Long reviewId, Long reviewerId, String reviewerNickname,
        Boolean isFollowingUser, String reviewContent, Integer reviewRating, Long reviewLikeCount,
        boolean isMe, List<String> tags, List<String> reviewImages) {
        this.reviewId = reviewId;
        this.reviewerId = reviewerId;
        this.reviewerNickname = reviewerNickname;
        this.isFollowingUser = isFollowingUser;
        this.isMe = isMe;
        this.reviewContent = reviewContent;
        this.reviewRating = reviewRating;
        this.reviewLikeCount = reviewLikeCount;
        this.reviewerTags = tags;
        this.reviewImages = reviewImages;
    }

    public static ReadReviewResponseDto of(ReadReviewQueryDto queryDto, User loginUser,
        List<ReviewImage> reviewImages, List<UserTag> userTags) {
        List<String> reviewImageUrls =
            reviewImages != null ? reviewImages.stream().map(ReviewImage::getImageUrl).toList()
                : List.of();
        List<String> tags =
            userTags != null ? userTags.stream().map(userTag -> userTag.getTag().getName()).toList()
                : List.of();
        return ReadReviewResponseDto.builder()
            .reviewContent(queryDto.getContent())
            .reviewId(queryDto.getReviewId())
            .reviewerId(queryDto.getReviewerId())
            .reviewRating(queryDto.getRating())
            .reviewLikeCount(queryDto.getLikeCount())
            .isFollowingUser(queryDto.isFollowing())
            .isMe(loginUser != null && loginUser.getId() == queryDto.getReviewerId())
            .reviewerNickname(queryDto.getReviewerNickname())
            .reviewImages(reviewImageUrls)
            .tags(tags)
            .build();
    }
}
