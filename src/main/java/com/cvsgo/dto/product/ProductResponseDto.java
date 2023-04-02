package com.cvsgo.dto.product;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

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
    private final List<ConvenienceStoreEventDto> convenienceStoreEvents;

    @Builder
    public ProductResponseDto(Long productId, String productName, Integer productPrice,
        String productImageUrl, Long categoryId, String manufacturerName, Boolean isLiked,
        Boolean isBookmarked, Long reviewCount, Double reviewRating,
        List<ConvenienceStoreEventDto> convenienceStoreEvents) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productImageUrl = productImageUrl;
        this.categoryId = categoryId;
        this.manufacturerName = manufacturerName;
        this.isLiked = isLiked;
        this.reviewCount = reviewCount.intValue();
        this.isBookmarked = isBookmarked;
        this.reviewRating = reviewRating(reviewRating);
        this.convenienceStoreEvents = convenienceStoreEvents;
    }

    public static ProductResponseDto of(SearchProductQueryDto searchProductQueryDto,
        List<ConvenienceStoreEventDto> convenienceStoreEvents) {
        return ProductResponseDto.builder()
            .productId(searchProductQueryDto.getProductId())
            .productName(searchProductQueryDto.getProductName())
            .productPrice(searchProductQueryDto.getProductPrice())
            .productImageUrl(searchProductQueryDto.getProductImageUrl())
            .categoryId(searchProductQueryDto.getCategoryId())
            .manufacturerName(searchProductQueryDto.getManufacturerName())
            .isLiked(searchProductQueryDto.getIsLiked())
            .isBookmarked(searchProductQueryDto.getIsBookmarked())
            .reviewCount(searchProductQueryDto.getReviewCount())
            .reviewRating(searchProductQueryDto.getAvgRating())
            .convenienceStoreEvents(convenienceStoreEvents)
            .build();
    }

    private String reviewRating(Double reviewRating) {
        if (reviewRating != null) {
            return String.format("%.1f", reviewRating);
        }
        return "0";
    }
}
