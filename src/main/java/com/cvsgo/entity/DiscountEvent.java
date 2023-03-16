package com.cvsgo.entity;

import com.cvsgo.entity.EventType.Values;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue(Values.DISCOUNT)
@Entity
public class DiscountEvent extends Event {

    private Integer discountAmount;

    @Builder
    public DiscountEvent(Long id, String eventType, Product product,
        ConvenienceStore convenienceStore, Integer discountAmount) {
        super(id, eventType, product, convenienceStore);
        this.discountAmount = discountAmount;
    }
}
