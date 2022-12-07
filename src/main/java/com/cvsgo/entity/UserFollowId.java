package com.cvsgo.entity;

import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class UserFollowId implements Serializable {

    private User following;
    private User follower;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserFollowId that = (UserFollowId) o;
        return Objects.equals(following, that.following) && Objects.equals(follower,
            that.follower);
    }

    @Override
    public int hashCode() {
        return Objects.hash(following, follower);
    }
}
