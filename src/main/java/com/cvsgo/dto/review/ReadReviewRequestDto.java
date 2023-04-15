package com.cvsgo.dto.review;

import java.util.List;
import lombok.Getter;

@Getter
public class ReadReviewRequestDto {

    private final List<Long> tagIds;

    private final List<Integer> ratings;

    private final ReviewSortBy sortBy;

    public ReadReviewRequestDto(List<Long> tagIds, List<Integer> ratings, ReviewSortBy sortBy) {
        this.tagIds = tagIds;
        this.ratings = ratings;
        this.sortBy = sortBy;
    }
}
