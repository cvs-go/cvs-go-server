package com.cvsgo.dto.product;

import com.cvsgo.entity.Category;
import com.cvsgo.entity.ConvenienceStore;
import com.cvsgo.entity.EventType;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductSearchFilter {

    private List<ConvenienceStore> convenienceStore;
    private List<Category> category;
    private List<EventType> event;
    private List<Integer> price;

    private ProductSearchFilter(List<ConvenienceStore> convenienceStore, List<Category> category,
        List<EventType> event, List<Integer> price) {
        this.convenienceStore = convenienceStore;
        this.category = category;
        this.event = event;
        this.price = price;
    }

    public static ProductSearchFilter of(List<ConvenienceStore> convenienceStore,
        List<Category> category, List<EventType> event, List<Integer> price) {
        return new ProductSearchFilter(convenienceStore, category, event, price);
    }
}
