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
    private Integer lowestPrice;
    private Integer highestPrice;

    private ProductSearchFilter(List<ConvenienceStore> convenienceStores, List<Category> categories,
        List<EventType> events, Integer lowestPrice, Integer highestPrice) {
        this.convenienceStores = convenienceStores;
        this.categories = categories;
        this.events = events;
        this.lowestPrice = lowestPrice;
        this.highestPrice = highestPrice;
    }

    public static ProductSearchFilter of(List<ConvenienceStore> convenienceStore,
        List<Category> category, List<EventType> event, Integer lowestPrice, Integer highestPrice) {
        return new ProductSearchFilter(convenienceStore, category, event, lowestPrice, highestPrice);
    }
}
