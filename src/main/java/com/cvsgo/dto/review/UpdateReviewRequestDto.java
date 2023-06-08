package com.cvsgo.dto.review;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Getter
@NoArgsConstructor
public class UpdateReviewRequestDto {

    @NotNull(message = "평점은 필수입니다")
    @Range(min = 1, max = 5, message = "평점은 1점 이상 5점 이하여야 합니다.")
    private Integer rating;

    @Size(min = 1, max = 1000, message = "리뷰는 1자 이상 1000자 이하여야 합니다.")
    private String content;

    private List<String> imageUrls;

    public UpdateReviewRequestDto(Integer rating, String content, List<String> imageUrls) {
        this.rating = rating;
        this.content = content;
        this.imageUrls = imageUrls;
    }
}
