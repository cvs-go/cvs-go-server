package com.cvsgo.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventType {

    BOGO(Values.BOGO),
    BTGO(Values.BTGO),
    GIFT(Values.GIFT),
    DISCOUNT(Values.DISCOUNT);

    private final String value;

    public static class Values {
        public static final String BOGO = "BOGO";
        public static final String BTGO = "BTGO";
        public static final String GIFT = "GIFT";
        public static final String DISCOUNT = "DISCOUNT";
    }
}
