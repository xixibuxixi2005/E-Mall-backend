package com.whut.emall.business.dto;

import io.micrometer.common.lang.NonNull;
import lombok.Data;

@Data
public class RegisterDTO {
    @NonNull String email;
    @NonNull String password;
    @NonNull String verificationCode;
    
    String username;
    String phone;
}
