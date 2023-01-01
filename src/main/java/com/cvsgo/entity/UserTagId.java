package com.cvsgo.entity;

import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class UserTagId implements Serializable {

    private User user;
    private Tag tag;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserTagId userTagId = (UserTagId) o;
        return Objects.equals(user, userTagId.user) && Objects.equals(tag,
            userTagId.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, tag);
    }
}
