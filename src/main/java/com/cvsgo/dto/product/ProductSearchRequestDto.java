package com.cvsgo.dto.product;

import jakarta.persistence.criteria.CriteriaBuilder.In;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductSearchRequestDto {

    private List<Long> convenienceStore;
    private List<Long> category;
    private List<String> event;
    private List<Integer> price;

    @Builder
    public ProductSearchRequestDto(List<Long> convenienceStore, List<Long> category,
        List<String> event, List<Integer> price) {
        this.convenienceStore = convenienceStore;
        this.category = category;
        this.event = event;
        this.price = price;
    }
}
