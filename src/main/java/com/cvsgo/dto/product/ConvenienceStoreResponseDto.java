package com.cvsgo.dto.product;

import com.cvsgo.entity.ConvenienceStore;
import lombok.Getter;

@Getter
public class ConvenienceStoreResponseDto {

    private final Long id;
    private final String name;

    private ConvenienceStoreResponseDto(ConvenienceStore convenienceStore) {
        this.id = convenienceStore.getId();
        this.name = convenienceStore.getName();
    }

    public static ConvenienceStoreResponseDto from(ConvenienceStore convenienceStore) {
        return new ConvenienceStoreResponseDto(convenienceStore);
    }
}
