package com.cvsgo.dto.product;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductDetailResponseDto {

    private final Long productId;
    private final String productName;
    private final Integer productPrice;
    private final String productImageUrl;
    private final String manufacturerName;
    private final Boolean isLiked;
    private final Boolean isBookmarked;
    private final List<ConvenienceStoreEventDto> convenienceStoreEvents;

    @Builder
    public ProductDetailResponseDto(Long productId, String productName, Integer productPrice,
        String productImageUrl, String manufacturerName, Boolean isLiked, Boolean isBookmarked,
        List<ConvenienceStoreEventDto> convenienceStoreEvents) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productImageUrl = productImageUrl;
        this.manufacturerName = manufacturerName;
        this.isLiked = isLiked;
        this.isBookmarked = isBookmarked;
        this.convenienceStoreEvents = convenienceStoreEvents;
    }

    public static ProductDetailResponseDto of(
        SearchProductDetailQueryDto searchProductDetailQueryDto,
        List<ConvenienceStoreEventDto> convenienceStoreEvents) {
        return ProductDetailResponseDto.builder()
            .productId(searchProductDetailQueryDto.getProductId())
            .productName(searchProductDetailQueryDto.getProductName())
            .productPrice(searchProductDetailQueryDto.getProductPrice())
            .productImageUrl(searchProductDetailQueryDto.getProductImageUrl())
            .manufacturerName(searchProductDetailQueryDto.getManufacturerName())
            .isLiked(searchProductDetailQueryDto.getIsLiked())
            .isBookmarked(searchProductDetailQueryDto.getIsBookmarked())
            .convenienceStoreEvents(convenienceStoreEvents)
            .build();
    }
}
