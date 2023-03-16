package com.cvsgo.dto.product;

import com.cvsgo.entity.ConvenienceStore;
import com.cvsgo.entity.Event;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
public class SellAtResponseDto {

    private final String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String event;

    private SellAtResponseDto(ConvenienceStore convenienceStore, Event event) {
        this.name = convenienceStore.getName();
        this.event = event != null ? event.getEventType() : null;
    }

    public static SellAtResponseDto of(ConvenienceStore convenienceStore, Event event) {
        return new SellAtResponseDto(convenienceStore, event);
    }
}
