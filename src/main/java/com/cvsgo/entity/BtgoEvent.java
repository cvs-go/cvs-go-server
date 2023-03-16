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
@DiscriminatorValue(Values.BTGO)
@Entity
public class BtgoEvent extends Event {

    @Builder
    public BtgoEvent(Long id, String eventType, Product product, ConvenienceStore convenienceStore) {
        super(id, eventType, product, convenienceStore);
    }
}
