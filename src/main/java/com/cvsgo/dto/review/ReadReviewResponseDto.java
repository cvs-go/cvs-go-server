package com.cvsgo.dto.review;

import java.util.List;
import lombok.Getter;

@Getter
public class ReadReviewResponseDto {

    private final long latestReviewCount;

    private final List<ReviewDto> reviews;

    private ReadReviewResponseDto(long latestReviewCount, List<ReviewDto> reviews) {
        this.latestReviewCount = latestReviewCount;
        this.reviews = reviews;
    }

    public static ReadReviewResponseDto of(long latestReviewCount, List<ReviewDto> reviews) {
        return new ReadReviewResponseDto(latestReviewCount, reviews);
    }
}
