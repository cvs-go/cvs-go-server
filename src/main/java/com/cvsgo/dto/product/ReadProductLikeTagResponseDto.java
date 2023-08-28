package com.cvsgo.dto.product;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class ReadProductLikeTagResponseDto {

    private final Long id;
    private final String name;
    private final Long tagCount;

    @QueryProjection
    public ReadProductLikeTagResponseDto(Long id, String name, Long tagCount) {
        this.id = id;
        this.name = name;
        this.tagCount = tagCount;
    }
}
