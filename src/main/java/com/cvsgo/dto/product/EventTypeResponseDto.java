package com.cvsgo.dto.product;

import com.cvsgo.entity.EventType;
import lombok.Getter;

@Getter
public class EventTypeResponseDto {

    private final String value;
    private final String name;

    private EventTypeResponseDto(EventType eventType) {
        this.value = eventType.getValue();
        this.name = eventType.getName();
    }

    public static EventTypeResponseDto from(EventType eventType) {
        return new EventTypeResponseDto(eventType);
    }
}
