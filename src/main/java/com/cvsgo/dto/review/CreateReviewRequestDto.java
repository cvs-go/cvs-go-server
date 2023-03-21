package com.cvsgo.dto.review;

import com.cvsgo.entity.Product;
import com.cvsgo.entity.Review;
import com.cvsgo.entity.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
public class CreateReviewRequestDto {

    @NotNull(message = "평점은 필수입니다")
    @Range(min = 1, max = 5, message = "평점은 1점 이상 5점 이하여야 합니다.")
    private final Integer rating;

    @Size(min = 1, max = 1000, message = "리뷰는 1자 이상 1000자 이하여야 합니다.")
    private final String content;

    private final List<MultipartFile> images;

    @Builder
    public CreateReviewRequestDto(Integer rating, String content, List<MultipartFile> images) {
        this.rating = rating;
        this.content = content;
        this.images = images;
    }

    public Review toEntity(User user, Product product, List<String> imageUrls) {
        return Review.builder()
                .user(user)
                .product(product)
                .rating(rating)
                .content(content)
                .imageUrls(imageUrls)
                .build();
    }

}
