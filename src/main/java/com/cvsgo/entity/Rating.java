package com.cvsgo.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Rating {

    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5);

    private final int rating;

    public static Rating valueOf(int rating) {
        for (Rating r : values()) {
            if (r.getRating() == rating) {
                return r;
            }
        }
        return null;
    }
}
