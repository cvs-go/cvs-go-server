package com.cvsgo.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    GUEST("ROLE_GUEST"),
    ASSOCIATE("ROLE_ASSOCIATE"),
    REGULAR("ROLE_REGULAR");

    private final String key;

}
