package com.cvsgo.dto.product;

import com.cvsgo.entity.EventType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class ConvenienceStoreEventQueryDto {

    private final Long productId;

    private final String convenienceStoreName;

    private final EventType eventType;

    @QueryProjection
    public ConvenienceStoreEventQueryDto(Long productId, String convenienceStoreName,
        EventType eventType) {
        this.productId = productId;
        this.convenienceStoreName = convenienceStoreName;
        this.eventType = eventType;
    }
}
