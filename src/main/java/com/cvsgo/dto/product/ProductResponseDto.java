package com.cvsgo.dto.product;

import com.cvsgo.entity.Product;
import com.querydsl.core.annotations.QueryProjection;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductResponseDto {

    private final Long productId;
    private final String productName;
    private final Integer productPrice;
    private final String productImageUrl;
    private final Long categoryId;
    private final String manufacturerName;
    private final Boolean isLiked;
    private final Boolean isBookmarked;
    private final Integer reviewCount;
    private final String reviewRating;
    private List<SellAtResponseDto> sellAt;

    @Builder
    @QueryProjection
    public ProductResponseDto(Long productId, String productName, Integer productPrice,
        String productImageUrl, Long categoryId, String manufacturerName, Integer isLiked,
        Integer isBookmarked, Long reviewCount, Double reviewRating) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productImageUrl = productImageUrl;
        this.categoryId = categoryId;
        this.manufacturerName = manufacturerName;
        this.isLiked = isLiked != null ? Boolean.TRUE : Boolean.FALSE;
        this.isBookmarked = isBookmarked != null ? Boolean.TRUE : Boolean.FALSE;
        this.reviewCount = reviewCount.intValue();
        this.reviewRating = reviewRating(reviewRating);
    }

    public static ProductResponseDto of(Product product, Integer isLiked, Integer isBookmarked,
        Long reviewCount, Double reviewRating) {
        return ProductResponseDto.builder()
            .productId(product.getId())
            .productName(product.getName())
            .productPrice(product.getPrice())
            .productImageUrl(product.getImageUrl())
            .categoryId(product.getCategory().getId())
            .manufacturerName(product.getManufacturer().getName())
            .isLiked(isLiked)
            .isBookmarked(isBookmarked)
            .reviewCount(reviewCount)
            .reviewRating(reviewRating)
            .build();
    }

    private String reviewRating(Double reviewRating) {
        if (reviewRating != null) {
            return String.format("%.1f", reviewRating);
        }
        return "0";
    }
}
