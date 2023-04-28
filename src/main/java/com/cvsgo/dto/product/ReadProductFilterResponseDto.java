package com.cvsgo.dto.product;

import java.util.List;
import lombok.Getter;

@Getter
public class ReadProductFilterResponseDto {

    private final List<ConvenienceStoreDto> convenienceStores;
    private final List<CategoryDto> categories;
    private final List<EventTypeDto> eventTypes;
    private final Integer highestPrice;

    private ReadProductFilterResponseDto(List<ConvenienceStoreDto> convenienceStores,
        List<CategoryDto> categories, List<EventTypeDto> eventTypes,
        Integer highestPrice) {
        this.convenienceStores = convenienceStores;
        this.categories = categories;
        this.eventTypes = eventTypes;
        this.highestPrice = highestPrice;
    }

    public static ReadProductFilterResponseDto of(List<ConvenienceStoreDto> convenienceStores,
        List<CategoryDto> categories, List<EventTypeDto> eventTypes,
        Integer highestPrice) {
        return new ReadProductFilterResponseDto(convenienceStores, categories, eventTypes,
            highestPrice);
    }
}
