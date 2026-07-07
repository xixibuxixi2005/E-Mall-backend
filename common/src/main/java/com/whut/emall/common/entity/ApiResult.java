package com.whut.emall.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResult {
    int code;
    String msg;
    Object data;

    public ApiResult(String message){
        this(200, message, null);
    }
    public ApiResult(int code, String message){
        this(code, message, null);
    }
    public ApiResult(String message, Object data){
        this(200, message, data);
    }

    public static ApiResult ok(String message){
        return new ApiResult(message);
    }
    public static ApiResult ok(String message, Object data){
        return new ApiResult(message, data);
    }
}
