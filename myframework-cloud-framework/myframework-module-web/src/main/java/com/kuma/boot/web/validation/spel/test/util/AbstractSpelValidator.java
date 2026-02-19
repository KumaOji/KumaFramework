/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 */
package com.kuma.boot.web.validation.spel.test.util;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.web.validation.spel.core.SpelValidContext;
import com.kuma.boot.web.validation.spel.core.result.FieldError;
import com.kuma.boot.web.validation.spel.core.result.ObjectValidResult;

import java.util.Collection;
import java.util.List;

public abstract class AbstractSpelValidator {
    public abstract ObjectValidResult validate(Object var1, String[] var2, SpelValidContext var3);

    public boolean checkConstraintResult(List<VerifyObject> verifyObjectList) {
        int failCount = 0;
        for (VerifyObject verifyObject : verifyObjectList) {
            if (this.checkConstraintResult(verifyObject)) continue;
            ++failCount;
        }
        return failCount == 0;
    }

    public boolean checkConstraintResult(VerifyObject verifyObject) {
        Object object = verifyObject.getObject();
        String[] spelGroups = verifyObject.getSpelGroups();
        SpelValidContext context = verifyObject.getContext();
        Collection<VerifyFailedField> verifyFailedFields = verifyObject.getVerifyFailedFields();
        boolean expectException = verifyObject.isExpectException();
        LogContext.setValidateObject(object);
        LogUtils.info((String)"Start checking object: {}", (Object[])new Object[]{object});
        int failCount = 0;
        try {
            ObjectValidResult validResult = this.validate(object, spelGroups, context);
            failCount += this.processVerifyResult(verifyFailedFields, ConstraintViolationSet.of(validResult.getErrors()));
        }
        catch (Exception e) {
            if (expectException) {
                LogUtils.info((String)"Passed, Capture exception {}, message: {}", (Object[])new Object[]{e.getClass(), e.getMessage()});
                expectException = false;
            }
            LogUtils.error((String)"Failed, Capture exception {}, message: {}", (Object[])new Object[]{e.getClass(), e.getMessage(), e});
            ++failCount;
        }
        if (expectException) {
            LogUtils.error((String)"Failed, No exception captured", (Object[])new Object[0]);
            ++failCount;
        }
        if (failCount == 0) {
            LogUtils.info((String)"Verification end, all passed", (Object[])new Object[0]);
        } else {
            LogUtils.error((String)"Verification end, number of failures: {}", (Object[])new Object[]{failCount});
        }
        LogUtils.info((String)"------------------------------------------------------------------------", (Object[])new Object[0]);
        LogContext.clearValidateObject();
        return failCount == 0;
    }

    private int processVerifyResult(Collection<VerifyFailedField> verifyFailedFields, ConstraintViolationSet violationSet) {
        String fieldNameLogKey = "fieldName";
        int failCount = 0;
        for (VerifyFailedField verifyFailedField : verifyFailedFields) {
            String fieldName = verifyFailedField.getName();
            LogContext.set("fieldName", fieldName);
            String message = verifyFailedField.getMessage();
            LogUtils.info((String)"Expected exception information: {}", (Object[])new Object[]{message == null ? "ignore" : message});
            boolean fieldMatch = false;
            boolean find = false;
            FieldError fieldError = violationSet.getAndRemove(fieldName, message);
            if (fieldError != null) {
                find = true;
                LogUtils.info((String)"Real exception information: {}", (Object[])new Object[]{fieldError.getErrorMessage()});
                if (message != null && !message.equals(fieldError.getErrorMessage())) {
                    LogUtils.error((String)"Failed", (Object[])new Object[0]);
                } else {
                    fieldMatch = true;
                    LogUtils.info((String)"Passed", (Object[])new Object[0]);
                }
            }
            if (!find) {
                LogUtils.error((String)"Excess field", (Object[])new Object[0]);
            }
            if (fieldMatch) continue;
            ++failCount;
        }
        LogContext.remove("fieldName");
        for (FieldError violation : violationSet.getAll()) {
            LogUtils.error((String)"Field [{}] is ignored", (Object[])new Object[]{violation.getFieldName()});
            ++failCount;
        }
        return failCount;
    }
}

