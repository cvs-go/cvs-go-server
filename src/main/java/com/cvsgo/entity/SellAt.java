package com.cvsgo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@IdClass(SellAtId.class)
public class SellAt extends BaseTimeEntity {

    @Id
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Id
    @ManyToOne
    @JoinColumn(name = "convenience_store_id")
    private ConvenienceStore convenienceStore;

    @Builder
    public SellAt(Product product, ConvenienceStore convenienceStore) {
        this.product = product;
        this.convenienceStore = convenienceStore;
    }
}
