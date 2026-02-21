package com.kuma.boot.web.validation.spel.core.result;


/**
 * 字段错误信息
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/4/29
 */
public class FieldError {
    @Override
    public String toString() {
        return "FieldError{" +
                "fieldName='" + fieldName + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * 字段名称
     */
    private final String fieldName;

    /**
     * 错误信息
     */
    private final String errorMessage;

    public FieldError(String fieldName, String errorMessage) {
        this.fieldName = fieldName;
        this.errorMessage = errorMessage;
    }

    public static FieldError of(String fieldName, String errorMessage) {
        return new FieldError(fieldName, errorMessage);
    }
}
