package com.cvsgo.dto.review;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class UpdateReviewRequestDto {

    @NotNull(message = "평점은 필수입니다")
    @Range(min = 1, max = 5, message = "평점은 1점 이상 5점 이하여야 합니다.")
    private final Integer rating;

    @Size(min = 1, max = 1000, message = "리뷰는 1자 이상 1000자 이하여야 합니다.")
    private final String content;

    private final List<MultipartFile> images = new ArrayList<>();

    public UpdateReviewRequestDto(Integer rating, String content, List<MultipartFile> images) {
        this.rating = rating;
        this.content = content;
        if (images != null) {
            this.images.addAll(images);
        }
    }
}
