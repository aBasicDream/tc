package com.tcyh.common.exceptions;

import com.tcyh.common.constant.ErrorCodeEnum;

/**
 * 业务异常
 */
public class BusinessException extends RuntimeException{

    public BusinessException(ErrorCodeEnum errorCodeEnum, String detail) {
        super(errorCodeEnum.getMessage() + ": " + detail);
    }
}
