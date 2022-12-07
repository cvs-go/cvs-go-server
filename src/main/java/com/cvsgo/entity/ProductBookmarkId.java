package com.cvsgo.entity;

import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class ProductBookmarkId implements Serializable {

    private User user;
    private Product product;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProductBookmarkId that = (ProductBookmarkId) o;
        return Objects.equals(user, that.user) && Objects.equals(product,
            that.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, product);
    }
}
