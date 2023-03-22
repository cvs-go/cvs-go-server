package com.cvsgo.exception.product;

import com.cvsgo.dto.ErrorResponse;
import com.cvsgo.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProductExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NotFoundProductException.class)
    public ErrorResponse handleProductNotFoundException(NotFoundProductException e) {
        return ErrorResponse.from(ErrorCode.NOT_FOUND_PRODUCT);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NotFoundProductLikeException.class)
    public ErrorResponse handleNotFoundProductLikeException(NotFoundProductLikeException e) {
        return ErrorResponse.from(ErrorCode.NOT_FOUND_PRODUCT_LIKE);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateProductLikeException.class)
    public ErrorResponse handleDuplicateProductLikeException(DuplicateProductLikeException e) {
        return ErrorResponse.of(e.getMessage(), e.getCode());
    }

}
