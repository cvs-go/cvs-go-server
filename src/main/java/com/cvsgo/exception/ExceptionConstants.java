package com.cvsgo.exception;

import com.cvsgo.exception.user.DuplicateEmailException;
import com.cvsgo.exception.user.DuplicateNicknameException;

public interface ExceptionConstants {

    DuplicateEmailException DUPLICATE_EMAIL = new DuplicateEmailException(ErrorCode.DUPLICATE_EMAIL);
    DuplicateNicknameException DUPLICATE_NICKNAME = new DuplicateNicknameException(ErrorCode.DUPLICATE_NICKNAME);

}
