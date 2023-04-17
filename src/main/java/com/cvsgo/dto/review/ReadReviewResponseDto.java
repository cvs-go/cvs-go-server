package com.cvsgo.dto.review;

import com.cvsgo.entity.ReviewImage;
import com.cvsgo.entity.User;
import com.cvsgo.entity.UserTag;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReadReviewResponseDto {

    private final Long reviewId;

    private final Long reviewerId;

    private final String reviewerNickname;

    private final String reviewerProfileImageUrl;

    private final Boolean isFollowingUser;

    private final Boolean isMe;

    private final String reviewContent;

    private final Integer reviewRating;

    private final Boolean isReviewLiked;

    private final Long reviewLikeCount;

    private final List<String> reviewerTags;

    private final List<String> reviewImages;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime createdAt;

    @Builder
    private ReadReviewResponseDto(Long reviewId, Long reviewerId, String reviewerNickname,
        String reviewerProfileImageUrl, Boolean isFollowingUser, String reviewContent,
        Integer reviewRating, Boolean isReviewLiked, Long reviewLikeCount, boolean isMe,
        List<String> tags, List<String> reviewImages, LocalDateTime createdAt) {
        this.reviewId = reviewId;
        this.reviewerId = reviewerId;
        this.reviewerNickname = reviewerNickname;
        this.reviewerProfileImageUrl = reviewerProfileImageUrl;
        this.isFollowingUser = isFollowingUser;
        this.isMe = isMe;
        this.reviewContent = reviewContent;
        this.reviewRating = reviewRating;
        this.isReviewLiked = isReviewLiked;
        this.reviewLikeCount = reviewLikeCount;
        this.reviewerTags = tags;
        this.reviewImages = reviewImages;
        this.createdAt = createdAt;
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
            .isReviewLiked(queryDto.isReviewLiked())
            .reviewLikeCount(queryDto.getLikeCount())
            .isFollowingUser(queryDto.isFollowing())
            .isMe(loginUser != null && loginUser.getId() == queryDto.getReviewerId())
            .reviewerNickname(queryDto.getReviewerNickname())
            .reviewerProfileImageUrl(queryDto.getReviewerProfileImageUrl())
            .reviewImages(reviewImageUrls)
            .createdAt(queryDto.getCreatedAt())
            .tags(tags)
            .build();
    }
}
