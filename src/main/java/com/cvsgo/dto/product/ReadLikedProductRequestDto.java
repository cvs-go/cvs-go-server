package com.cvsgo.dto.product;

import lombok.Getter;

@Getter
public class ReadLikedProductRequestDto {

    private final ProductSortBy sortBy;

    public ReadLikedProductRequestDto(ProductSortBy sortBy) {
        this.sortBy = sortBy;
    }
}
