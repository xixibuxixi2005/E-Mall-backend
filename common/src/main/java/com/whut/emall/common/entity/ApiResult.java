package com.whut.emall.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResult<T> {
    int code;
    String msg;
    T data;

    public ApiResult(String message){
        this(200, message, null);
    }
    public ApiResult(int code, String message){
        this(code, message, null);
    }
    public ApiResult(String message, T data){
        this(200, message, data);
    }

    public static ApiResult<Void> ok(String message){
        return new ApiResult<>(message);
    }
    public static <T> ApiResult<T> ok(String message, T data){
        return new ApiResult<T>(message, data);
    }
}
