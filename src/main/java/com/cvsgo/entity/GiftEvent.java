package com.cvsgo.entity;

import com.cvsgo.entity.EventType.Values;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue(Values.GIFT)
@Entity
public class GiftEvent extends Event {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gift_product_id")
    private Product giftProduct;

    @Builder
    public GiftEvent(Long id, Product product, ConvenienceStore convenienceStore,
        Product giftProduct) {
        super(id, EventType.GIFT, product, convenienceStore);
        this.giftProduct = giftProduct;
    }
}
