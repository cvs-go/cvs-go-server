package com.cvsgo.exception;

import com.cvsgo.exception.auth.InvalidPasswordException;
import com.cvsgo.exception.auth.NotFoundUserException;
import com.cvsgo.exception.auth.UnauthorizedUserException;
import com.cvsgo.exception.product.DuplicateProductBookmarkException;
import com.cvsgo.exception.product.DuplicateProductLikeException;
import com.cvsgo.exception.product.NotFoundProductException;
import com.cvsgo.exception.product.NotFoundProductLikeException;
import com.cvsgo.exception.user.DuplicateEmailException;
import com.cvsgo.exception.user.DuplicateNicknameException;

public interface ExceptionConstants {

    DuplicateEmailException DUPLICATE_EMAIL = new DuplicateEmailException(ErrorCode.DUPLICATE_EMAIL);
    DuplicateNicknameException DUPLICATE_NICKNAME = new DuplicateNicknameException(ErrorCode.DUPLICATE_NICKNAME);
    NotFoundUserException NOT_FOUND_USER = new NotFoundUserException(ErrorCode.NOT_FOUND_USER);
    InvalidPasswordException INVALID_PASSWORD = new InvalidPasswordException(ErrorCode.INVALID_PASSWORD);
    UnauthorizedUserException UNAUTHORIZED_USER = new UnauthorizedUserException(ErrorCode.UNAUTHORIZED_USER);
    NotFoundProductException NOT_FOUND_PRODUCT = new NotFoundProductException(ErrorCode.NOT_FOUND_PRODUCT);
    DuplicateProductLikeException DUPLICATE_PRODUCT_LIKE = new DuplicateProductLikeException(ErrorCode.DUPLICATE_PRODUCT_LIKE);
    NotFoundProductLikeException NOT_FOUND_PRODUCT_LIKE = new NotFoundProductLikeException(ErrorCode.NOT_FOUND_PRODUCT_LIKE);
    DuplicateProductBookmarkException DUPLICATE_PRODUCT_BOOKMARK = new DuplicateProductBookmarkException(ErrorCode.DUPLICATE_PRODUCT_BOOKMARK);

}
