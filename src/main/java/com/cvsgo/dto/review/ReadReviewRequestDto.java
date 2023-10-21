package com.cvsgo.dto.review;

import java.util.List;
import lombok.Getter;

@Getter
public class ReadReviewRequestDto {

    private final ReviewSortBy sortBy;

    private final List<Long> categoryIds;

    private final List<Long> tagIds;

    private final List<Integer> ratings;

    public ReadReviewRequestDto(ReviewSortBy sortBy, List<Long> categoryIds, List<Long> tagIds,
        List<Integer> ratings) {
        this.sortBy = sortBy;
        this.categoryIds = categoryIds;
        this.tagIds = tagIds;
        this.ratings = ratings;
    }
}
