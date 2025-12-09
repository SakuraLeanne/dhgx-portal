package com.dhgx.portal.common.exception;

import com.dhgx.portal.common.model.ErrorCode;
import lombok.Getter;

/**
 * Generic runtime exception that carries standardized error information.
 */
@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String detailMessage) {
        super(detailMessage);
        this.errorCode = errorCode;
    }
}
