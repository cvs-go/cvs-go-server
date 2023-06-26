package com.cvsgo.exception;

public interface ExceptionConstants {

    BadRequestException BAD_REQUEST_USER_FOLLOW = new BadRequestException(ErrorCode.INVALID_USER_FOLLOW);

    UnauthorizedException INVALID_PASSWORD = new UnauthorizedException(ErrorCode.INVALID_PASSWORD);
    UnauthorizedException UNAUTHORIZED_USER = new UnauthorizedException(ErrorCode.UNAUTHORIZED_USER);

    BadRequestException INVALID_FILE_SIZE = new BadRequestException(ErrorCode.INVALID_FILE_SIZE);

    ForbiddenException FORBIDDEN_REVIEW = new ForbiddenException(ErrorCode.FORBIDDEN_REVIEW);

    NotFoundException NOT_FOUND_USER = new NotFoundException(ErrorCode.NOT_FOUND_USER);
    NotFoundException NOT_FOUND_USER_FOLLOW = new NotFoundException(ErrorCode.NOT_FOUND_USER_FOLLOW);
    NotFoundException NOT_FOUND_PRODUCT = new NotFoundException(ErrorCode.NOT_FOUND_PRODUCT);
    NotFoundException NOT_FOUND_PRODUCT_LIKE = new NotFoundException(ErrorCode.NOT_FOUND_PRODUCT_LIKE);
    NotFoundException NOT_FOUND_PRODUCT_BOOKMARK = new NotFoundException(ErrorCode.NOT_FOUND_PRODUCT_BOOKMARK);
    NotFoundException NOT_FOUND_REVIEW = new NotFoundException(ErrorCode.NOT_FOUND_REVIEW);
    NotFoundException NOT_FOUND_REVIEW_LIKE = new NotFoundException(ErrorCode.NOT_FOUND_REVIEW_LIKE);

    DuplicateException DUPLICATE_EMAIL = new DuplicateException(ErrorCode.DUPLICATE_EMAIL);
    DuplicateException DUPLICATE_NICKNAME = new DuplicateException(ErrorCode.DUPLICATE_NICKNAME);
    DuplicateException DUPLICATE_USER_FOLLOW = new DuplicateException(ErrorCode.DUPLICATE_USER_FOLLOW);
    DuplicateException DUPLICATE_PRODUCT_LIKE = new DuplicateException(ErrorCode.DUPLICATE_PRODUCT_LIKE);
    DuplicateException DUPLICATE_PRODUCT_BOOKMARK = new DuplicateException(ErrorCode.DUPLICATE_PRODUCT_BOOKMARK);
    DuplicateException DUPLICATE_REVIEW = new DuplicateException(ErrorCode.DUPLICATE_REVIEW);
    DuplicateException DUPLICATE_REVIEW_LIKE = new DuplicateException(ErrorCode.DUPLICATE_REVIEW_LIKE);

}
