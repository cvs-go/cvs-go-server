package com.cvsgo.dto.product;

import lombok.Getter;

@Getter
public class ReadUserProductRequestDto {

    private final ProductSortBy sortBy;

    public ReadUserProductRequestDto(ProductSortBy sortBy) {
        this.sortBy = sortBy;
    }
}
