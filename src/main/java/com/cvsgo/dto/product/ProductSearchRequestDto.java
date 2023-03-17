package com.cvsgo.dto.product;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductSearchRequestDto {

    private List<Long> convenienceStoreIds;
    private List<Long> categoryIds;
    private List<String> eventTypes;
    private List<Integer> prices;

    @Builder
    public ProductSearchRequestDto(List<Long> convenienceStoreIds, List<Long> categoryIds,
        List<String> eventTypes, List<Integer> prices) {
        this.convenienceStoreIds = convenienceStoreIds;
        this.categoryIds = categoryIds;
        this.eventTypes = eventTypes;
        this.prices = prices;
    }
}
