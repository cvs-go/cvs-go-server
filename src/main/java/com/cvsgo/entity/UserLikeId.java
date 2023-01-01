package com.cvsgo.entity;

import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class UserLikeId implements Serializable {

    private User user;
    private Review review;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserLikeId that = (UserLikeId) o;
        return Objects.equals(user, that.user) && Objects.equals(review,
            that.review);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, review);
    }
}
