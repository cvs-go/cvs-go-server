package com.cvsgo.dto.product;

import com.cvsgo.entity.EventType;
import java.util.List;
import lombok.Getter;

@Getter
public class ReadProductRequestDto {

    private final ProductSortBy sortBy;

    private Boolean isEvent;

    private List<Long> convenienceStoreIds;

    private List<Long> categoryIds;

    private List<EventType> eventTypes;

    private Integer lowestPrice;

    private Integer highestPrice;

    private String keyword;

    public ReadProductRequestDto(ProductSortBy sortBy, Boolean isEvent,
        List<Long> convenienceStoreIds, List<Long> categoryIds, List<EventType> eventTypes,
        Integer lowestPrice, Integer highestPrice, String keyword) {
        this.sortBy = sortBy;
        this.isEvent = isEvent;
        this.convenienceStoreIds = convenienceStoreIds;
        this.categoryIds = categoryIds;
        this.eventTypes = eventTypes;
        this.lowestPrice = lowestPrice;
        this.highestPrice = highestPrice;
        this.keyword = keyword;
    }
}
