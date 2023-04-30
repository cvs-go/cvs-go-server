package com.cvsgo.dto.review;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReadReviewRequestDto {

    @Enumerated(EnumType.STRING)
    private final ReviewSortBy sortBy;

    private final List<Long> categoryIds;

    private final List<Long> tagIds;

    private final List<Integer> ratings;

    @Builder
    public ReadReviewRequestDto(ReviewSortBy sortBy, List<Long> categoryIds, List<Long> tagIds,
        List<Integer> ratings) {
        this.sortBy = sortBy;
        this.categoryIds = categoryIds;
        this.tagIds = tagIds;
        this.ratings = ratings;
    }
}
