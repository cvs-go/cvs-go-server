package com.cvsgo.dto.product;

import com.cvsgo.entity.ProductBookmark;
import com.cvsgo.entity.ProductLike;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class ReadProductQueryDto {

    private Long productId;

    private String productName;

    private Integer productPrice;

    private String productImageUrl;

    private Long categoryId;

    private String manufacturerName;

    private Boolean isLiked;

    private Boolean isBookmarked;

    private Long reviewCount;

    private Double avgRating;

    private Double score;

    @QueryProjection
    public ReadProductQueryDto(Long productId, String productName, Integer productPrice, String productImageUrl,
        Long categoryId, String manufacturerName, ProductLike productLike,
        ProductBookmark productBookmark, Long reviewCount, Double avgRating, Double score) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productImageUrl = productImageUrl;
        this.categoryId = categoryId;
        this.manufacturerName = manufacturerName;
        this.isLiked = productLike != null;
        this.isBookmarked = productBookmark != null;
        this.reviewCount = reviewCount;
        this.avgRating = avgRating;
        this.score = score;
    }
}
