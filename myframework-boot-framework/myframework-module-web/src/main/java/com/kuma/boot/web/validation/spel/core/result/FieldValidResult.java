/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.kuma.boot.web.validation.spel.core.result;

import java.util.Arrays;
import org.jetbrains.annotations.NotNull;

public class FieldValidResult {
    private boolean success;
    @NotNull
    private String message = "";
    @NotNull
    private String fieldName = "";
    private Object[] args;

    public String toString() {
        return "FieldValidResult{success=" + this.success + ", message='" + this.message + "', fieldName='" + this.fieldName + "', args=" + Arrays.toString(this.args) + "}";
    }

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @NotNull
    public String getMessage() {
        return this.message;
    }

    public void setMessage(@NotNull String message) {
        this.message = message;
    }

    @NotNull
    public String getFieldName() {
        return this.fieldName;
    }

    public void setFieldName(@NotNull String fieldName) {
        this.fieldName = fieldName;
    }

    public Object[] getArgs() {
        return this.args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public static FieldValidResult of(boolean success) {
        return FieldValidResult.of(success, "");
    }

    public static FieldValidResult of(boolean success, @NotNull String message) {
        return FieldValidResult.of(success, message, "", null);
    }

    public static FieldValidResult of(boolean success, Object ... args) {
        return FieldValidResult.of(success, "", "", args);
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
        return FieldValidResult.of(true);
    }
}

