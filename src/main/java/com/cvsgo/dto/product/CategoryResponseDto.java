package com.cvsgo.dto.product;

import com.cvsgo.entity.Category;
import lombok.Getter;

@Getter
public class CategoryResponseDto {

    private final Long id;
    private final String name;

    private CategoryResponseDto(Category category) {
        this.id = category.getId();
        this.name = category.getName();
    }

    public static CategoryResponseDto from(Category category) {
        return new CategoryResponseDto(category);
    }
}
