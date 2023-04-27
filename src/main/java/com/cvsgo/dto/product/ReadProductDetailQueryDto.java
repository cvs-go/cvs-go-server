package com.cvsgo.dto.product;

import com.cvsgo.entity.ProductBookmark;
import com.cvsgo.entity.ProductLike;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class ReadProductDetailQueryDto {

    private final Long productId;
    private final String productName;
    private final Integer productPrice;
    private final String productImageUrl;
    private final String manufacturerName;
    private final Boolean isLiked;
    private final Boolean isBookmarked;

    @QueryProjection
    public ReadProductDetailQueryDto(Long productId, String productName, Integer productPrice,
        String productImageUrl, String manufacturerName, ProductLike productLike,
        ProductBookmark productBookmark) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productImageUrl = productImageUrl;
        this.manufacturerName = manufacturerName;
        this.isLiked = productLike != null;
        this.isBookmarked = productBookmark != null;
    }
}
