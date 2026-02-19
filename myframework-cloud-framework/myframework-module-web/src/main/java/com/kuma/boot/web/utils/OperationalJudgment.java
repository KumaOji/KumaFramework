/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.enums.ResultEnum
 *  com.kuma.boot.common.enums.UserEnum
 *  com.kuma.boot.common.exception.BusinessException
 *  com.kuma.boot.common.utils.reflect.ReflectionUtils
 *  com.kuma.boot.security.spring.utils.SecurityUtils
 */
package com.kuma.boot.web.utils;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.UserEnum;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.utils.reflect.ReflectionUtils;
import com.kuma.boot.security.spring.utils.SecurityUtils;
import java.util.Objects;

public final class OperationalJudgment {
    public static <T> T judgment(T object) {
        return OperationalJudgment.judgment(object, "memberId", "storeId");
    }

    public static <T> T judgment(T object, String buyerIdField, String storeIdField) {
        Integer type = SecurityUtils.getCurrentUser().getType();
        UserEnum userEnum = UserEnum.getEnumByCode((int)type);
        switch (Objects.requireNonNull(userEnum)) {
            case MANAGER: {
                return object;
            }
            case MEMBER: {
                if (SecurityUtils.getCurrentUser().getUserId().equals(ReflectionUtils.getFieldValue(object, (String)buyerIdField))) {
                    return object;
                }
                throw new BusinessException(ResultEnum.USER_AUTHORITY_ERROR);
            }
            case STORE: {
                if (SecurityUtils.getCurrentUser().getStoreId().equals(ReflectionUtils.getFieldValue(object, (String)storeIdField))) {
                    return object;
                }
                throw new BusinessException(ResultEnum.USER_AUTHORITY_ERROR);
            }
        }
        throw new BusinessException(ResultEnum.USER_AUTHORITY_ERROR);
    }
}

