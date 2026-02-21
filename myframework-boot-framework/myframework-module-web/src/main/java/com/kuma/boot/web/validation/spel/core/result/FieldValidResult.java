package com.kuma.boot.web.validation.spel.core.result;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * 字段校验结果
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/4/29
 */
public class FieldValidResult {
    @Override
    public String toString() {
        return "FieldValidResult{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", args=" + Arrays.toString(args) +
                '}';
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @NotNull
    public String getMessage() {
        return message;
    }

    public void setMessage(@NotNull String message) {
        this.message = message;
    }

    @NotNull
    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(@NotNull String fieldName) {
        this.fieldName = fieldName;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    /**
     * 校验结果，true表示校验通过，false表示校验失败
     */
    private boolean success;

    /**
     * 校验失败时的错误信息
     * <p>
     * 当校验结果为false时，会将错误信息添加到最终的结果中，若此字段为null，则使用默认的错误信息
     */
    @NotNull
    private String message = "";

    /**
     * 验证的字段名称，用于校验失败时构建错误信息
     */
    @NotNull
    private String fieldName = "";

    /**
     * 用于错误信息的占位符替换参数
     */
    private Object[] args;

    public static FieldValidResult of(boolean success) {
        return of(success, "");
    }

    public static FieldValidResult of(boolean success, @NotNull String message) {
        return of(success, message, "", null);
    }

    public static FieldValidResult of(boolean success, Object... args) {
        return of(success, "", "", args);
    }

    public static FieldValidResult of(boolean success, @NotNull String message, String fieldName, Object[] args) {
        FieldValidResult result = new FieldValidResult();
        result.success = success;
        result.fieldName = fieldName;
        result.message = message;
        result.args = args;
        return result;
    }

    public static FieldValidResult success() {
        return of(true);
    }

}
