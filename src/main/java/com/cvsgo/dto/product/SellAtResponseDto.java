package com.cvsgo.dto.product;

import com.cvsgo.entity.ConvenienceStore;
import com.cvsgo.entity.Event;
import com.cvsgo.entity.EventType;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;

@Getter
public class SellAtResponseDto {

    private final String name;

    @Enumerated(EnumType.STRING)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final EventType eventType;

    private SellAtResponseDto(ConvenienceStore convenienceStore, Event eventType) {
        this.name = convenienceStore.getName();
        this.eventType = eventType != null ? eventType.getEventType() : null;
    }

    public static SellAtResponseDto of(ConvenienceStore convenienceStore, Event event) {
        return new SellAtResponseDto(convenienceStore, event);
    }
}
