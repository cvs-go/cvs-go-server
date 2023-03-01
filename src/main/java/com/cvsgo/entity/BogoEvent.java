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
@DiscriminatorValue(Values.BOGO)
@Entity
public class BogoEvent extends Event {

    @Builder
    public BogoEvent(Long id, Product product, ConvenienceStore convenienceStore) {
        super(id, product, convenienceStore);
    }
}
