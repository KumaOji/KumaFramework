/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 */
package com.kuma.boot.office.easyexcel.easyexcelimport.valid;

import cn.hutool.core.util.StrUtil;
import com.kuma.boot.office.easyexcel.easyexcelimport.annotation.ImportFieldValid;
import com.kuma.boot.office.easyexcel.easyexcelimport.model.ImportCommonConstant;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

public class ImportValid {
    public static void validRequireField(Object object, List<String> errorLog) {
        Field[] fields;
        StringBuilder log = new StringBuilder();
        for (Field field : fields = object.getClass().getDeclaredFields()) {
            String message;
            field.setAccessible(Boolean.TRUE);
            Object fieldValue = null;
            boolean isValid = field.isAnnotationPresent(ImportFieldValid.class);
            if (!isValid) continue;
            try {
                fieldValue = field.get(object);
            }
            catch (Exception e) {
                throw new RuntimeException("\u6821\u9a8c\u5fc5\u586b\u5b57\u6bb5\u65f6\u51fa\u73b0\u5f02\u5e38," + e.getMessage());
            }
            if (Objects.nonNull(fieldValue) && field.getType().getName().contains("String")) {
                if (!StrUtil.isBlank((CharSequence)fieldValue.toString())) continue;
                message = field.getAnnotation(ImportFieldValid.class).message();
                log.append(message).append(",");
                continue;
            }
            if (!Objects.isNull(fieldValue)) continue;
            message = field.getAnnotation(ImportFieldValid.class).message();
            log.append(message).append(",");
        }
        if (log.length() > ImportCommonConstant.ZERO) {
            errorLog.add(StrUtil.removeSuffix((CharSequence)log.toString(), (CharSequence)","));
        }
    }
}

