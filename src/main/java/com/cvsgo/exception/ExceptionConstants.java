package com.cvsgo.exception;

import com.cvsgo.exception.auth.InvalidPasswordException;
import com.cvsgo.exception.auth.NotFoundUserException;
import com.cvsgo.exception.auth.UnauthorizedUserException;
import com.cvsgo.exception.product.DuplicateProductBookmarkException;
import com.cvsgo.exception.product.DuplicateProductLikeException;
import com.cvsgo.exception.product.NotFoundProductBookmarkException;
import com.cvsgo.exception.product.NotFoundProductException;
import com.cvsgo.exception.product.NotFoundProductLikeException;
import com.cvsgo.exception.review.NotFoundReviewException;
import com.cvsgo.exception.user.BadRequestUserFollowException;
import com.cvsgo.exception.user.DuplicateEmailException;
import com.cvsgo.exception.user.DuplicateNicknameException;
import com.cvsgo.exception.user.DuplicateUserFollowException;
import com.cvsgo.exception.user.ForbiddenUserException;

public interface ExceptionConstants {

    DuplicateEmailException DUPLICATE_EMAIL = new DuplicateEmailException(ErrorCode.DUPLICATE_EMAIL);
    DuplicateNicknameException DUPLICATE_NICKNAME = new DuplicateNicknameException(ErrorCode.DUPLICATE_NICKNAME);
    NotFoundUserException NOT_FOUND_USER = new NotFoundUserException(ErrorCode.NOT_FOUND_USER);
    BadRequestUserFollowException BAD_REQUEST_USER_FOLLOW = new BadRequestUserFollowException(ErrorCode.BAD_REQUEST_USER_FOLLOW);
    DuplicateUserFollowException DUPLICATE_USER_FOLLOW = new DuplicateUserFollowException(ErrorCode.DUPLICATE_USER_FOLLOW);
    InvalidPasswordException INVALID_PASSWORD = new InvalidPasswordException(ErrorCode.INVALID_PASSWORD);
    UnauthorizedUserException UNAUTHORIZED_USER = new UnauthorizedUserException(ErrorCode.UNAUTHORIZED_USER);
    ForbiddenUserException FORBIDDEN_USER = new ForbiddenUserException(ErrorCode.FORBIDDEN_USER);
    NotFoundProductException NOT_FOUND_PRODUCT = new NotFoundProductException(ErrorCode.NOT_FOUND_PRODUCT);
    DuplicateProductLikeException DUPLICATE_PRODUCT_LIKE = new DuplicateProductLikeException(ErrorCode.DUPLICATE_PRODUCT_LIKE);
    NotFoundProductLikeException NOT_FOUND_PRODUCT_LIKE = new NotFoundProductLikeException(ErrorCode.NOT_FOUND_PRODUCT_LIKE);
    DuplicateProductBookmarkException DUPLICATE_PRODUCT_BOOKMARK = new DuplicateProductBookmarkException(ErrorCode.DUPLICATE_PRODUCT_BOOKMARK);
    NotFoundProductBookmarkException NOT_FOUND_PRODUCT_BOOKMARK = new NotFoundProductBookmarkException(ErrorCode.NOT_FOUND_PRODUCT_BOOKMARK);
    NotFoundReviewException NOT_FOUND_REVIEW = new NotFoundReviewException(ErrorCode.NOT_FOUND_REVIEW);

}
