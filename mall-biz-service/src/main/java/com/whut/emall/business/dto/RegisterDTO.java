package com.whut.emall.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterDTO {
    @NotBlank(message = "email不能为空") String email;
    @NotNull(message = "password不能为空") String password;
    @NotBlank(message = "verificationCode不能为空") String verificationCode;
    
    String username;
    String phone;
}
