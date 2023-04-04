package com.cvsgo.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    DUPLICATE_EMAIL("DUPLICATE_EMAIL", "중복된 이메일입니다."),
    DUPLICATE_NICKNAME("DUPLICATE_NICKNAME", "중복된 닉네임입니다."),
    DUPLICATE_PRODUCT_LIKE("DUPLICATE_PRODUCT_LIKE", "중복된 상품 좋아요입니다."),
    DUPLICATE_PRODUCT_BOOKMARK("DUPLICATE_PRODUCT_BOOKMARK", "중복된 상품 북마크입니다."),

    NOT_FOUND_USER("NOT_FOUND_USER", "존재하지 않는 사용자입니다."),
    NOT_FOUND_PRODUCT("NOT_FOUND_PRODUCT", "존재하지 않는 상품입니다."),
    NOT_FOUND_PRODUCT_LIKE("NOT_FOUND_PRODUCT_LIKE", "존재하지 않는 상품 좋아요입니다."),
    NOT_FOUND_PRODUCT_BOOKMARK("NOT_FOUND_PRODUCT_BOOKMARK", "존재하지 않는 상품 북마크입니다."),
    NOT_FOUND_REVIEW("NOT_FOUND_REVIEW", "존재하지 않는 리뷰입니다."),

    INVALID_PASSWORD("INVALID_PASSWORD", "비밀번호가 일치하지 않습니다."),
    UNAUTHORIZED_USER("UNAUTHORIZED_USER", "권한이 없는 사용자입니다."),
    EXPIRED_TOKEN("EXPIRED_TOKEN", "만료된 토큰입니다.");

    private final String code;
    private final String message;

}
