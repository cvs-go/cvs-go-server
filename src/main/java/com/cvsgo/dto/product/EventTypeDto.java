package com.cvsgo.dto.product;

import com.cvsgo.entity.EventType;
import lombok.Getter;

@Getter
public class EventTypeDto {

    private final String value;
    private final String name;

    private EventTypeDto(EventType eventType) {
        this.value = eventType.getValue();
        this.name = eventType.getName();
    }

    public static EventTypeDto from(EventType eventType) {
        return new EventTypeDto(eventType);
    }
}
