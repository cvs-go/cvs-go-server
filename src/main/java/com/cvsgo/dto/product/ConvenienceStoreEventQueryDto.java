package com.cvsgo.dto.product;

import com.cvsgo.entity.Event;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class ConvenienceStoreEventQueryDto {

    private final Long productId;

    private final String convenienceStoreName;

    private final Event event;

    @QueryProjection
    public ConvenienceStoreEventQueryDto(Long productId, String convenienceStoreName, Event event) {
        this.productId = productId;
        this.convenienceStoreName = convenienceStoreName;
        this.event = event;
    }
}
