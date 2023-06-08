package com.cvsgo.dto.review;

import com.cvsgo.entity.Product;
import com.cvsgo.entity.Review;
import com.cvsgo.entity.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Getter
@NoArgsConstructor
public class CreateReviewRequestDto {

    @NotNull(message = "평점은 필수입니다")
    @Range(min = 1, max = 5, message = "평점은 1점 이상 5점 이하여야 합니다.")
    private Integer rating;

    @Size(min = 1, max = 1000, message = "리뷰는 1자 이상 1000자 이하여야 합니다.")
    private String content;

    private List<String> imageUrls;

    @Builder
    public CreateReviewRequestDto(Integer rating, String content, List<String> imageUrls) {
        this.rating = rating;
        this.content = content;
        this.imageUrls = imageUrls;
    }

    public Review toEntity(User user, Product product) {
        return Review.builder()
            .user(user)
            .product(product)
            .rating(rating)
            .content(content)
            .imageUrls(imageUrls)
            .build();
    }

}
