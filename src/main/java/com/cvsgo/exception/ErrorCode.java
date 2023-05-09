package com.cvsgo.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    /* 400 BAD_REQUEST */
    INVALID_REQUEST("유효성 검사에 실패했습니다."),
    INVALID_USER_FOLLOW("본인을 팔로우할 수 없습니다."),

    /* 401 UNAUTHORIZED */
    UNAUTHORIZED_USER("로그인을 하지 않은 사용자입니다."),
    INVALID_PASSWORD("비밀번호가 일치하지 않습니다."),
    EXPIRED_TOKEN("만료된 토큰입니다."),

    /* 409 CONFLICT */
    DUPLICATE_EMAIL("중복된 이메일입니다."),
    DUPLICATE_NICKNAME("중복된 닉네임입니다."),
    DUPLICATE_PRODUCT_LIKE( "중복된 상품 좋아요입니다."),
    DUPLICATE_PRODUCT_BOOKMARK("중복된 상품 북마크입니다."),
    DUPLICATE_USER_FOLLOW("중복된 회원 팔로우입니다."),
    DUPLICATE_REVIEW("한 상품에 대한 리뷰는 한 번만 작성할 수 있습니다."),
    DUPLICATE_REVIEW_LIKE( "하나의 리뷰에 좋아요는 한 번만 할 수 있습니다."),

    /* 403 FORBIDDEN */
    FORBIDDEN_REVIEW("해당 리뷰에 대한 권한이 없습니다."),

    /* 404 NOT_FOUND */
    NOT_FOUND_USER("존재하지 않는 사용자입니다."),
    NOT_FOUND_USER_FOLLOW("존재하지 않는 팔로우입니다."),
    NOT_FOUND_PRODUCT("존재하지 않는 상품입니다."),
    NOT_FOUND_PRODUCT_LIKE("존재하지 않는 상품 좋아요입니다."),
    NOT_FOUND_PRODUCT_BOOKMARK("존재하지 않는 상품 북마크입니다."),
    NOT_FOUND_REVIEW("존재하지 않는 리뷰입니다."),

    /* 500 INTERNAL_SEVER_ERROR */
    UNEXPECTED_ERROR("시스템에 문제가 발생했습니다.");

    private final String message;

}
