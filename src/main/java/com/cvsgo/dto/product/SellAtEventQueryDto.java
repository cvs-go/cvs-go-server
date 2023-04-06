package com.cvsgo.dto.product;

import com.cvsgo.entity.Event;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class SellAtEventQueryDto {

    private final Long convenienceStoreId;

    private final String convenienceStoreName;

    private final Event event;

    @QueryProjection
    public SellAtEventQueryDto(Long convenienceStoreId, String convenienceStoreName, Event event) {
        this.convenienceStoreId = convenienceStoreId;
        this.convenienceStoreName = convenienceStoreName;
        this.event = event;
    }
}
