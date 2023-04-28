package com.cvsgo.dto.product;

import com.cvsgo.entity.EventType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReadProductRequestDto {

    @Enumerated(EnumType.STRING)
    private final ProductSortBy sortBy;

    private List<Long> convenienceStoreIds;

    private List<Long> categoryIds;

    @Enumerated(EnumType.STRING)
    private List<EventType> eventTypes;

    private Integer lowestPrice;

    private Integer highestPrice;

    private String keyword;

    public ReadProductRequestDto(ProductSortBy sortBy, List<Long> convenienceStoreIds,
        List<Long> categoryIds, List<EventType> eventTypes, Integer lowestPrice,
        Integer highestPrice, String keyword) {
        this.sortBy = sortBy;
        this.convenienceStoreIds = convenienceStoreIds;
        this.categoryIds = categoryIds;
        this.eventTypes = eventTypes;
        this.lowestPrice = lowestPrice;
        this.highestPrice = highestPrice;
        this.keyword = keyword;
    }
}
