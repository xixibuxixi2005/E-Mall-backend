package com.whut.emall.business.config;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.whut.emall.common.entity.ApiException;
import com.whut.emall.common.entity.ApiResult;


@RestControllerAdvice
public class GlobalExceptionHandler{
    @ExceptionHandler(Exception.class)
    public ApiResult handleException(Exception err) {
        err.printStackTrace();
        return ApiException.err(err.getLocalizedMessage()).toResult();
    }
}
