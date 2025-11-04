package com.musinsa.point.exception;

import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.musinsa.point.dto.CommonResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
//@Order(Ordered.HIGHEST_PRECEDENCE) handler 여러개 있을 시 순서 지정
@RestControllerAdvice
public class GlobalExceptionHandler {

    //내부 커스텀 Exception
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<CommonResponseDto> handleApiException(ApiException ex){
        log.error("ApiException, errorCodeName = {}, errorMsg = {}", ex.getErrorCode().name(), ex.getMessage());
        CommonResponseDto res = CommonResponseDto.fail("#{domainErrorCode}", ex.getMessage());
        return ResponseEntity.status(ex.getErrorCode().getHttpStatus()).body(res);
    }

    //@valid 등 Exception 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponseDto> handleValidException(MethodArgumentNotValidException ex){
        log.error("MethodArgumentNotValidException");
        String msg = ErrorCode.INVALID_REQUEST.getMessage();
        if(ex.getAllErrors().stream().findFirst().isPresent())
            msg = String.valueOf(ex.getAllErrors().stream().findFirst().get().getDefaultMessage());
        CommonResponseDto res = CommonResponseDto.fail("#{domainErrorCode}", msg);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    //Lock 관련 Exception 처리
    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<CommonResponseDto> handleLockException(OptimisticLockingFailureException ex){
        log.error("OptimisticLockingFailureException");
        CommonResponseDto res = CommonResponseDto.fail("#{domainErrorCode}", ErrorCode.LOCK_ERROR.getMessage());
        return ResponseEntity.status(ErrorCode.LOCK_ERROR.getHttpStatus()).body(res);
    }

    //Exception 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponseDto> handleException(Exception ex){
        log.error("Exception");
        CommonResponseDto res = CommonResponseDto.fail("#{domainErrorCode}", ErrorCode.INTERNAL_ERROR.getMessage());
        return ResponseEntity.status(ErrorCode.INTERNAL_ERROR.getHttpStatus()).body(res);
    }
}
