package com.whut.emall.business.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterDTO {
    @NotBlank(message = "email不能为空")
    @Email(message = "邮箱格式错误") String email;
    @Size(min = 6, max = 20, message = "密码长度需为6-20位")
    @NotNull(message = "password不能为空") String password;
    @NotBlank(message = "verificationCode不能为空") String verificationCode;
    
    String username;
    String phone;
}
