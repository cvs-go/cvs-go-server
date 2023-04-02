package com.cvsgo.dto.product;

import com.cvsgo.entity.EventType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SearchProductRequestDto {

    private List<Long> convenienceStoreIds;

    private List<Long> categoryIds;

    @Enumerated(EnumType.STRING)
    private List<EventType> eventTypes;

    private Integer lowestPrice;

    private Integer highestPrice;

    @Builder
    public SearchProductRequestDto(List<Long> convenienceStoreIds, List<Long> categoryIds,
        List<EventType> eventTypes, Integer lowestPrice, Integer highestPrice) {
        this.convenienceStoreIds = convenienceStoreIds;
        this.categoryIds = categoryIds;
        this.eventTypes = eventTypes;
        this.lowestPrice = lowestPrice;
        this.highestPrice = highestPrice;
    }
}
