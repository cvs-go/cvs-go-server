package com.cvsgo.dto.product;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ReadProductDetailResponseDto {

    private final Long productId;
    private final String productName;
    private final Integer productPrice;
    private final String productImageUrl;
    private final String manufacturerName;
    private final Boolean isLiked;
    private final Boolean isBookmarked;
    private final List<ConvenienceStoreEventDto> convenienceStoreEvents;

    @Builder
    public ReadProductDetailResponseDto(Long productId, String productName, Integer productPrice,
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

    public static ReadProductDetailResponseDto of(
        ReadProductDetailQueryDto readProductDetailQueryDto,
        List<ConvenienceStoreEventDto> convenienceStoreEvents) {
        return ReadProductDetailResponseDto.builder()
            .productId(readProductDetailQueryDto.getProductId())
            .productName(readProductDetailQueryDto.getProductName())
            .productPrice(readProductDetailQueryDto.getProductPrice())
            .productImageUrl(readProductDetailQueryDto.getProductImageUrl())
            .manufacturerName(readProductDetailQueryDto.getManufacturerName())
            .isLiked(readProductDetailQueryDto.getIsLiked())
            .isBookmarked(readProductDetailQueryDto.getIsBookmarked())
            .convenienceStoreEvents(convenienceStoreEvents)
            .build();
    }
}
