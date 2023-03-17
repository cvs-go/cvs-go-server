package com.cvsgo.dto.product;

import com.cvsgo.entity.EventType;
import java.util.List;
import lombok.Getter;

@Getter
public class ProductFilterResponseDto {

    private final List<ConvenienceStoreResponseDto> convenienceStores;
    private final List<CategoryResponseDto> categories;
    private final List<EventTypeResponseDto> eventTypes;
    private final Integer highestPrice;

    private ProductFilterResponseDto(List<ConvenienceStoreResponseDto> convenienceStores,
        List<CategoryResponseDto> categories, List<EventTypeResponseDto> eventTypes,
        Integer highestPrice) {
        this.convenienceStores = convenienceStores;
        this.categories = categories;
        this.eventTypes = eventTypes;
        this.highestPrice = highestPrice;
    }

    public static ProductFilterResponseDto of(List<ConvenienceStoreResponseDto> convenienceStores,
        List<CategoryResponseDto> categories, List<EventTypeResponseDto> eventTypes,
        Integer highestPrice) {
        return new ProductFilterResponseDto(convenienceStores, categories, eventTypes,
            highestPrice);
    }
}
