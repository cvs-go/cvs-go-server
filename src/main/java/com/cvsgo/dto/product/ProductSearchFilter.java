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

    private List<ConvenienceStore> convenienceStores;
    private List<Category> categories;
    private List<EventType> events;
    private List<Integer> prices;

    private ProductSearchFilter(List<ConvenienceStore> convenienceStores, List<Category> categories,
        List<EventType> events, List<Integer> prices) {
        this.convenienceStores = convenienceStores;
        this.categories = categories;
        this.events = events;
        this.prices = prices;
    }

    public static ProductSearchFilter of(List<ConvenienceStore> convenienceStore,
        List<Category> category, List<EventType> event, List<Integer> price) {
        return new ProductSearchFilter(convenienceStore, category, event, price);
    }
}
