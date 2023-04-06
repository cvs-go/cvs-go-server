package com.cvsgo.dto.product;

import com.cvsgo.entity.ConvenienceStore;
import lombok.Getter;

@Getter
public class ConvenienceStoreDto {

    private final Long id;
    private final String name;

    private ConvenienceStoreDto(ConvenienceStore convenienceStore) {
        this.id = convenienceStore.getId();
        this.name = convenienceStore.getName();
    }

    public static ConvenienceStoreDto from(ConvenienceStore convenienceStore) {
        return new ConvenienceStoreDto(convenienceStore);
    }
}
