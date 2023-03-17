package com.cvsgo.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventType {

    BOGO(Values.BOGO, "1+1"),
    BTGO(Values.BTGO, "2+1"),
    GIFT(Values.GIFT, "증정"),
    DISCOUNT(Values.DISCOUNT, "할인");

    private final String value;
    private final String name;

    public static class Values {
        public static final String BOGO = "BOGO";
        public static final String BTGO = "BTGO";
        public static final String GIFT = "GIFT";
        public static final String DISCOUNT = "DISCOUNT";
    }
}
