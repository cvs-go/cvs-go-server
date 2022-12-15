package com.cvsgo.entity;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Rating {

    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5);

    private final int rating;

    public static Rating fromCode(Integer dbData) {
        return Arrays.stream(Rating.values())
            .filter(v -> v.getRating() == dbData)
            .findAny()
            .orElseThrow(
                () -> new IllegalArgumentException(String.format("순위에 %d가 존재하지 않습니다.", dbData)));
    }
}
