package com.cvsgo.exception;

import com.cvsgo.exception.user.DuplicateEmailException;

public interface ExceptionConstants {

    DuplicateEmailException DUPLICATE_EMAIL = new DuplicateEmailException(ErrorCode.DUPLICATE_EMAIL);
    DuplicateEmailException DUPLICATE_NICKNAME = new DuplicateEmailException(ErrorCode.DUPLICATE_NICKNAME);

}
