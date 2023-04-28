package com.cvsgo.dto.product;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReadProductResponseDto {

    private final Long productId;
    private final String productName;
    private final Integer productPrice;
    private final String productImageUrl;
    private final Long categoryId;
    private final String manufacturerName;
    private final Boolean isLiked;
    private final Boolean isBookmarked;
    private final Long reviewCount;
    private final String reviewRating;
    private final List<ConvenienceStoreEventDto> convenienceStoreEvents;

    @Builder
    public ReadProductResponseDto(Long productId, String productName, Integer productPrice,
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
        this.reviewCount = reviewCount;
        this.isBookmarked = isBookmarked;
        this.reviewRating = reviewRating(reviewRating);
        this.convenienceStoreEvents = convenienceStoreEvents;
    }

    public static ReadProductResponseDto of(ReadProductQueryDto readProductQueryDto,
        List<ConvenienceStoreEventDto> convenienceStoreEvents) {
        return ReadProductResponseDto.builder()
            .productId(readProductQueryDto.getProductId())
            .productName(readProductQueryDto.getProductName())
            .productPrice(readProductQueryDto.getProductPrice())
            .productImageUrl(readProductQueryDto.getProductImageUrl())
            .categoryId(readProductQueryDto.getCategoryId())
            .manufacturerName(readProductQueryDto.getManufacturerName())
            .isLiked(readProductQueryDto.getIsLiked())
            .isBookmarked(readProductQueryDto.getIsBookmarked())
            .reviewCount(readProductQueryDto.getReviewCount())
            .reviewRating(readProductQueryDto.getAvgRating())
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
