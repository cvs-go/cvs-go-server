package com.cvsgo.dto.product;

import com.cvsgo.entity.Manufacturer;
import com.cvsgo.entity.Product;
import com.querydsl.core.annotations.QueryProjection;
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
    private List<SellAtEventResponseDto> sellAts;

    @Builder
    @QueryProjection
    public ProductDetailResponseDto(Product product, Manufacturer manufacturer, Integer isLiked,
        Integer isBookmarked) {
        this.productId = product.getId();
        this.productName = product.getName();
        this.productPrice = product.getPrice();
        this.productImageUrl = product.getImageUrl();
        this.manufacturerName = manufacturer.getName();
        this.isLiked = isLiked != null ? Boolean.TRUE : Boolean.FALSE;
        this.isBookmarked = isBookmarked != null ? Boolean.TRUE : Boolean.FALSE;
    }

    public static ProductDetailResponseDto of(Product product, Manufacturer manufacturer,
        Integer isLiked, Integer isBookmarked) {
        return ProductDetailResponseDto.builder()
            .product(product)
            .manufacturer(manufacturer)
            .isLiked(isLiked)
            .isBookmarked(isBookmarked)
            .build();
    }
}
