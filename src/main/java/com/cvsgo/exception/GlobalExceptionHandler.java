package com.cvsgo.exception;

import com.cvsgo.dto.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    protected ErrorResponse handleBindException(BindException e) {
        log.info("유효성 검사 실패 - {} '{}'", e.getFieldError().getDefaultMessage(), e.getFieldError().getRejectedValue());
        return ErrorResponse.of(ErrorCode.INVALID_REQUEST.name(), e.getFieldError().getDefaultMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    protected ErrorResponse handleBadRequestException(BadRequestException e) {
        log.info(e.getMessage(), e);
        return ErrorResponse.from(e.getErrorCode());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(ExpiredJwtException.class)
    public ErrorResponse handleExpiredJwtException(ExpiredJwtException e) {
        log.info("만료된 토큰입니다.", e);
        return ErrorResponse.from(ErrorCode.EXPIRED_TOKEN);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(SignatureException.class)
    public ErrorResponse handleSignatureException(SignatureException e) {
        log.info("잘못된 시그니쳐", e);
        return ErrorResponse.from(ErrorCode.UNAUTHORIZED_USER);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public ErrorResponse handleUnauthorizedException(UnauthorizedException e) {
        log.info(e.getMessage(), e);
        return ErrorResponse.from(e.getErrorCode());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ForbiddenException.class)
    public ErrorResponse handleForbiddenException(ForbiddenException e) {
        log.info(e.getMessage(), e);
        return ErrorResponse.from(e.getErrorCode());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        log.info(e.getMessage(), e);
        return ErrorResponse.from(e.getErrorCode());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateException.class)
    public ErrorResponse handleDuplicateException(DuplicateException e) {
        log.info(e.getMessage(), e);
        return ErrorResponse.from(e.getErrorCode());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ErrorResponse handleObjectOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e) {
        log.info("상품 좋아요 동시성 문제 발생", e);
        return ErrorResponse.of(ErrorCode.UNEXPECTED_ERROR.name(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorResponse handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.info("데이터 무결성 위반 문제 발생", e);
        return ErrorResponse.of(ErrorCode.UNEXPECTED_ERROR.name(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ErrorResponse handleFileSizeLimitExceededException(MaxUploadSizeExceededException e) {
        log.info("파일 크기 초과", e);
        return ErrorResponse.from(ErrorCode.INVALID_FILE_SIZE);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IOException.class)
    public ErrorResponse handleIOException(IOException e) {
        log.error("IOException 발생", e);
        return ErrorResponse.of(ErrorCode.UNEXPECTED_ERROR.name(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleException(Exception e) {
        log.error("예기치 못한 예외가 발생했습니다.", e);
        return ErrorResponse.from(ErrorCode.UNEXPECTED_ERROR);
    }

}
