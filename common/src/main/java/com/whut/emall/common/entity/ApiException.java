package com.whut.emall.common.entity;

public class ApiException extends RuntimeException {
    int code;
    String msg;
    Object data;

    public ApiException(int code, String message, Object data) {
        super(message);
        this.code = code;
        this.msg = message;
        this.data = data;
    }

    public static ApiException err(String message) {
        return new ApiException(500, message, null);
    }

    public static ApiException err(String message, Object data) {
        return new ApiException(500, message, data);
    }

    public static ApiException err(int code, String message) {
        return new ApiException(code, message, null);
    }

    public static ApiException err(int code, String message, Object data) {
        return new ApiException(code, message, data);
    }

    public ApiResult<?> toResult() {
        return new ApiResult<>(code, msg, data);
    }
}
