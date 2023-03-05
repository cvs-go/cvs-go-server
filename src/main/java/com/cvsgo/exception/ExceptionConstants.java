package com.cvsgo.exception;

import com.cvsgo.exception.auth.NotFoundUserException;
import com.cvsgo.exception.auth.UnauthorizedUserException;
import com.cvsgo.exception.user.DuplicateEmailException;
import com.cvsgo.exception.user.DuplicateNicknameException;
import com.cvsgo.exception.auth.InvalidPasswordException;

public interface ExceptionConstants {

    DuplicateEmailException DUPLICATE_EMAIL = new DuplicateEmailException(ErrorCode.DUPLICATE_EMAIL);
    DuplicateNicknameException DUPLICATE_NICKNAME = new DuplicateNicknameException(ErrorCode.DUPLICATE_NICKNAME);
    NotFoundUserException NOT_FOUND_USER = new NotFoundUserException(ErrorCode.NOT_FOUND_USER);
    InvalidPasswordException INVALID_PASSWORD = new InvalidPasswordException(ErrorCode.INVALID_PASSWORD);
    UnauthorizedUserException UNAUTHORIZED_USER = new UnauthorizedUserException(ErrorCode.UNAUTHORIZED_USER);

}
