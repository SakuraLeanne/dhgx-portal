package com.dhgx.portal.common.util;

import com.dhgx.portal.common.exception.BusinessException;
import com.dhgx.portal.common.model.ErrorCode;

import java.util.Collection;
import java.util.Objects;

/**
 * Lightweight validation helpers to keep service layer code concise.
 */
public final class ValidationUtil {
    private ValidationUtil() {
    }

    public static void notNull(Object value, String fieldName) {
        if (Objects.isNull(value)) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, fieldName + " 不能为空");
        }
    }

    public static void notEmpty(Collection<?> collection, String fieldName) {
        if (collection == null || collection.isEmpty()) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, fieldName + " 不能为空");
        }
    }
}
