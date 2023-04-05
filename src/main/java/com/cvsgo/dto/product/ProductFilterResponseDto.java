package com.cvsgo.dto.product;

import java.util.List;
import lombok.Getter;

@Getter
public class ProductFilterResponseDto {

    private final List<ConvenienceStoreDto> convenienceStores;
    private final List<CategoryDto> categories;
    private final List<EventTypeDto> eventTypes;
    private final Integer highestPrice;

    private ProductFilterResponseDto(List<ConvenienceStoreDto> convenienceStores,
        List<CategoryDto> categories, List<EventTypeDto> eventTypes,
        Integer highestPrice) {
        this.convenienceStores = convenienceStores;
        this.categories = categories;
        this.eventTypes = eventTypes;
        this.highestPrice = highestPrice;
    }

    public static ProductFilterResponseDto of(List<ConvenienceStoreDto> convenienceStores,
        List<CategoryDto> categories, List<EventTypeDto> eventTypes,
        Integer highestPrice) {
        return new ProductFilterResponseDto(convenienceStores, categories, eventTypes,
            highestPrice);
    }
}
