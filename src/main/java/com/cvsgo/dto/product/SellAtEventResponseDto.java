package com.cvsgo.dto.product;

import com.cvsgo.entity.ConvenienceStore;
import com.cvsgo.entity.Event;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
public class SellAtEventResponseDto {

    private final Long convenienceStoreId;

    private final String convenienceStoreName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String eventType;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Integer discountAmount;

    private SellAtEventResponseDto(ConvenienceStore convenienceStore, Event event) {
        this.convenienceStoreId = convenienceStore.getId();
        this.convenienceStoreName = convenienceStore.getName();
        this.eventType = event != null ? event.getEventType().getName() : null;
        this.discountAmount = event != null ? event.getDiscountAmount() : null;
    }

    public static SellAtEventResponseDto of(ConvenienceStore convenienceStore, Event event) {
        return new SellAtEventResponseDto(convenienceStore, event);
    }
}
