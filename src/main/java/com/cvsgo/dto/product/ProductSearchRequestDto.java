package com.cvsgo.dto.product;

import com.cvsgo.entity.EventType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductSearchRequestDto {

    private List<Long> convenienceStoreIds;
    private List<Long> categoryIds;
    @Enumerated(EnumType.STRING)
    private List<EventType> eventTypes;
    private List<Integer> prices;

    @Builder
    public ProductSearchRequestDto(List<Long> convenienceStoreIds, List<Long> categoryIds,
        List<EventType> eventTypes, List<Integer> prices) {
        this.convenienceStoreIds = convenienceStoreIds;
        this.categoryIds = categoryIds;
        this.eventTypes = eventTypes;
        this.prices = prices;
    }
}
