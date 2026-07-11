package com.whut.emall.business.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginVO {
    String token;
    String refreshToken;
    String username;
    String roleCode;
    Integer userId;
    String phone;
}
