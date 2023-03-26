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

    private SellAtResponseDto(String convenienceStoreName, EventType eventType) {
        this.name = convenienceStoreName;
        this.eventType = eventType;
    }

    public static SellAtResponseDto of(String convenienceStoreName, EventType eventType) {
        return new SellAtResponseDto(convenienceStoreName, eventType);
    }
}
