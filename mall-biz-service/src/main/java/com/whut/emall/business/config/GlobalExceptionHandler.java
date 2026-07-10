package com.whut.emall.business.config;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.whut.emall.common.entity.ApiException;
import com.whut.emall.common.entity.ApiResult;

import jakarta.validation.ConstraintViolationException;


@RestControllerAdvice
public class GlobalExceptionHandler{
    @ExceptionHandler(ApiException.class)
    public ApiResult handleApiException(ApiException err) {
        return err.toResult();
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ApiResult handleValidationException(MethodArgumentNotValidException err) {
        return ApiException.err(400, "请求参数校验失败: " + err.getBindingResult().getFieldError().getDefaultMessage()).toResult();
    }

    @ExceptionHandler(BindException.class)
    public ApiResult handleValidationException(BindException err) {
        return ApiException.err(400, "请求参数校验失败: " + err.getBindingResult().getFieldError().getDefaultMessage()).toResult();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResult handleValidationException(ConstraintViolationException err) {
        return ApiException.err(400, "请求参数校验失败: " + err.getConstraintViolations().iterator().next().getMessage()).toResult();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResult handleHttpMessageNotReadableException(HttpMessageNotReadableException err) {
        return ApiException.err(400, "无效参数: " + err.getLocalizedMessage()).toResult();
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ApiResult handleNoResourceFoundException(NoResourceFoundException err) {
        return ApiException.err(404, "无效的请求路径: " + err.getResourcePath()).toResult();
    }

    @ExceptionHandler(Exception.class)
    public ApiResult handleException(Exception err) {
        err.printStackTrace();
        return ApiException.err(err.getLocalizedMessage()).toResult();
    }
}
