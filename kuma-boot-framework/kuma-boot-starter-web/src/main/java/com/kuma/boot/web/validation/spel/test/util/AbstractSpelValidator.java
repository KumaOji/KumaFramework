package com.kuma.boot.web.validation.spel.test.util;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.web.validation.spel.core.SpelValidContext;
import com.kuma.boot.web.validation.spel.core.result.FieldError;
import com.kuma.boot.web.validation.spel.core.result.ObjectValidResult;

import java.util.Collection;
import java.util.List;

/**
 * 测试验证工具抽象类
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/6/13
 */
public abstract class AbstractSpelValidator {

    /**
     * 参数校验
     * <p>
     * 调用此方法会触发约束校验
     *
     * @param obj        待验证对象
     * @param spelGroups spel 分组参数
     * @param context    验证上下文
     * @return 校验结果
     */
    public abstract ObjectValidResult validate(Object obj, String[] spelGroups, SpelValidContext context);

    /**
     * 验证约束结果是否符合预期
     */
    public boolean checkConstraintResult(List<VerifyObject> verifyObjectList) {
        int failCount = 0;
        for (VerifyObject verifyObject : verifyObjectList) {
            if (!checkConstraintResult(verifyObject)) {
                failCount++;
            }
        }
        return failCount == 0;
    }

    /**
     * 验证约束结果是否符合预期
     */
    public boolean checkConstraintResult( VerifyObject verifyObject) {
        Object object = verifyObject.getObject();
        String[] spelGroups = verifyObject.getSpelGroups();
        SpelValidContext context = verifyObject.getContext();
        Collection<VerifyFailedField> verifyFailedFields = verifyObject.getVerifyFailedFields();
        boolean expectException = verifyObject.isExpectException();

        // 设置日志上下文
        LogContext.setValidateObject(object);
        LogUtils.info("Start checking object: {}", object);

        int failCount = 0;
        try {
            // 执行约束校验
            ObjectValidResult validResult = validate(object, spelGroups, context);
            failCount += processVerifyResult(verifyFailedFields, ConstraintViolationSet.of(validResult.getErrors()));
        } catch (Exception e) {
            if (expectException) {
                LogUtils.info("Passed, Capture exception {}, message: {}", e.getClass(), e.getMessage());
                expectException = false;
            } else {
                LogUtils.error("Failed, Capture exception {}, message: {}", e.getClass(), e.getMessage(), e);
                failCount++;
            }
        }

        if (expectException) {
            LogUtils.error("Failed, No exception captured");
            failCount++;
        }

        if (failCount == 0) {
            LogUtils.info("Verification end, all passed");
        } else {
            LogUtils.error("Verification end, number of failures: {}", failCount);
        }
        LogUtils.info("------------------------------------------------------------------------");
        LogContext.clearValidateObject();

        return failCount == 0;
    }

    /**
     * 处理验证结果
     *
     * @param verifyFailedFields 预期失败字段
     * @param violationSet       验证结果
     * @return 验证失败的次数
     */
    private int processVerifyResult(Collection<VerifyFailedField> verifyFailedFields, ConstraintViolationSet violationSet) {
        final String fieldNameLogKey = "fieldName";
        int failCount = 0;
        // 检查结果是否符合预期
        for (VerifyFailedField verifyFailedField : verifyFailedFields) {
            String fieldName = verifyFailedField.getName();
            LogContext.set(fieldNameLogKey, fieldName);
            String message = verifyFailedField.getMessage();

            LogUtils.info("Expected exception information: {}", message == null ? "ignore" : message);

            boolean fieldMatch = false, find = false;

            FieldError fieldError = violationSet.getAndRemove(fieldName, message);
            if (fieldError != null) {
                find = true;
                LogUtils.info("Real exception information: {}", fieldError.getErrorMessage());

                // 异常信息不同时验证失败（没填写异常信息则不校验异常信息）
                if (message != null && !message.equals(fieldError.getErrorMessage())) {
                    LogUtils.error("Failed");
                } else {
                    fieldMatch = true;
                    LogUtils.info("Passed");
                }
            }

            if (!find) {
                // 多余的字段
                LogUtils.error("Excess field");
            }
            if (!fieldMatch) {
                failCount++;
            }
        }

        LogContext.remove(fieldNameLogKey);
        // 被忽略的字段
        for (FieldError violation : violationSet.getAll()) {
            LogUtils.error("Field [{}] is ignored", violation.getFieldName());
            failCount++;
        }
        return failCount;
    }

}
