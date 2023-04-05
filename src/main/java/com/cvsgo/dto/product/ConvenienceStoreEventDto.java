package com.cvsgo.dto.product;

import com.cvsgo.entity.Event;
import com.cvsgo.entity.EventType;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;

@Getter
public class ConvenienceStoreEventDto {

    private final String name;

    @Enumerated(EnumType.STRING)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final EventType eventType;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Integer discountAmount;

    private ConvenienceStoreEventDto(String convenienceStoreName, Event event) {
        this.name = convenienceStoreName;
        this.eventType = event != null ? event.getEventType() : null;
        this.discountAmount = event != null ? event.getDiscountAmount() : null;
    }

    public static ConvenienceStoreEventDto of(String convenienceStoreName, Event event) {
        return new ConvenienceStoreEventDto(convenienceStoreName, event);
    }
}
