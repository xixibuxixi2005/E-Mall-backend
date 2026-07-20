package com.whut.emall.business.vo;

import com.whut.emall.business.entity.enums.CSStatusStatus;

import lombok.Data;

@Data
public class CSStatusVO {
    Integer csId;
    String csName;
    CSStatusStatus status;
}
