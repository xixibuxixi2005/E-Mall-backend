package com.whut.emall.common.entity;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtPayload {
    long userId;
    String email;
    String roleCode;
}
