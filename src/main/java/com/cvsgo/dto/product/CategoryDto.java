package com.cvsgo.dto.product;

import com.cvsgo.entity.Category;
import lombok.Getter;

@Getter
public class CategoryDto {

    private final Long id;
    private final String name;

    private CategoryDto(Category category) {
        this.id = category.getId();
        this.name = category.getName();
    }

    public static CategoryDto from(Category category) {
        return new CategoryDto(category);
    }
}
