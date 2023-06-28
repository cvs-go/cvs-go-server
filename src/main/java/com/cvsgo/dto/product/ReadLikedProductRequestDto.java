package com.cvsgo.dto.product;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;

@Getter
public class ReadLikedProductRequestDto {

    @Enumerated(EnumType.STRING)
    private final ProductSortBy sortBy;

    public ReadLikedProductRequestDto(ProductSortBy sortBy) {
        this.sortBy = sortBy;
    }
}
