package com.cvsgo.dto.product;

import com.cvsgo.entity.Event;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
public class SellAtEventDto {

    private final Long convenienceStoreId;

    private final String convenienceStoreName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String eventType;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Integer discountAmount;

    private SellAtEventDto(Long convenienceStoreId, String convenienceStoreName, Event event) {
        this.convenienceStoreId = convenienceStoreId;
        this.convenienceStoreName = convenienceStoreName;
        this.eventType = event != null ? event.getEventType().getName() : null;
        this.discountAmount = event != null ? event.getDiscountAmount() : null;
    }

    public static SellAtEventDto of(Long convenienceStoreId, String convenienceStoreName, Event event) {
        return new SellAtEventDto(convenienceStoreId, convenienceStoreName, event);
    }
}
