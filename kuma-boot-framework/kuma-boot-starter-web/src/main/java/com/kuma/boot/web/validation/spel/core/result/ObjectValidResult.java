package com.kuma.boot.web.validation.spel.core.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 对象校验结果
 *
 * @author 阿杆
 * @version 1.1
 * @since 2024/4/29
 */
public class ObjectValidResult {

    private final List<com.kuma.boot.web.validation.spel.core.result.FieldError> errors = new ArrayList<>();

    public static final ObjectValidResult EMPTY = new ObjectValidResult();

    public boolean hasError() {
        return !errors.isEmpty();
    }

    public boolean noneError() {
        return errors.isEmpty();
    }

    public List<com.kuma.boot.web.validation.spel.core.result.FieldError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public int getErrorSize() {
        return errors.size();
    }

    /**
     * 添加校验结果
     * <p>
     * 当校验结果为false时，会将错误信息添加到结果中
     *
     * @param results 字段校验结果列表
     */
    public void addFieldResults(List<com.kuma.boot.web.validation.spel.core.result.FieldValidResult> results) {
        for (com.kuma.boot.web.validation.spel.core.result.FieldValidResult result : results) {
            this.addFieldResult(result);
        }
    }

    /**
     * 添加校验结果
     * <p>
     * 当校验结果为false时，会将错误信息添加到结果中
     *
     * @param result 字段校验结果
     */
    public void addFieldResult( com.kuma.boot.web.validation.spel.core.result.FieldValidResult result) {
        if (!result.isSuccess()) {
            errors.add(com.kuma.boot.web.validation.spel.core.result.FieldError.of(result.getFieldName(), result.getMessage()));
        }
    }

    public void addFieldError(List<com.kuma.boot.web.validation.spel.core.result.FieldError> fieldErrorList) {
        if (fieldErrorList != null && !fieldErrorList.isEmpty()) {
            errors.addAll(fieldErrorList);
        }
    }
}
