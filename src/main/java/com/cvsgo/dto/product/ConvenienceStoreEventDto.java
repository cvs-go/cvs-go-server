package com.cvsgo.dto.product;

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

    private ConvenienceStoreEventDto(String convenienceStoreName, EventType eventType) {
        this.name = convenienceStoreName;
        this.eventType = eventType;
    }

    public static ConvenienceStoreEventDto of(String convenienceStoreName, EventType eventType) {
        return new ConvenienceStoreEventDto(convenienceStoreName, eventType);
    }
}
