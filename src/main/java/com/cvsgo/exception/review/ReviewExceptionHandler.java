package com.cvsgo.exception.review;

import com.cvsgo.controller.ReviewController;
import com.cvsgo.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@RestControllerAdvice(assignableTypes = ReviewController.class)
public class ReviewExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IOException.class)
    public ErrorResponse handleIOException(IOException e) {
        return ErrorResponse.of("파일 처리 중 오류가 발생했습니다", "INTERNAL_SERVER_ERROR");
    }

}
