package com.whut.emall.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginDTO {
    @NotBlank(message = "email不能为空") String email;
    @NotNull(message = "password不能为空") String password;
}
