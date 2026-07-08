package com.whut.emall.business.dto;

import io.micrometer.common.lang.NonNull;
import lombok.Data;

@Data
public class LoginDTO {
    @NonNull String email;
    @NonNull String password;
}
