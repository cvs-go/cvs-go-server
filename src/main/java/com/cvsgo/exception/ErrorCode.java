package com.cvsgo.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    DUPLICATE_EMAIL("DUPLICATE_EMAIL", "중복된 이메일입니다."),
    DUPLICATE_NICKNAME("DUPLICATE_NICKNAME", "중복된 닉네임입니다.");

    private final String code;
    private final String message;

}
