/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  io.swagger.v3.oas.annotations.media.Schema
 */
package com.kuma.boot.common.model.result;

import cn.hutool.core.util.StrUtil;
import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.model.Code;
import com.kuma.boot.common.utils.id.IdGeneratorUtils;
import com.kuma.boot.common.utils.servlet.TraceUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.Instant;

@Schema(description="\u8fd4\u56de\u7ed3\u679c\u5bf9\u8c61")
public class Result<T>
implements Serializable {
    @Schema(description="\u72b6\u6001\u7801")
    private String status = StatusEnum.SUCCESS.name();
    @Schema(description="\u72b6\u6001\u7801")
    private String code = ResultEnum.SUCCESS.codeDesc();
    @Schema(description="\u72b6\u6001\u7801")
    private String message = CommonConstants.REQUEST_SUCCESS;
    @Schema(description="\u8bf7\u6c42\u7ed3\u675f\u65f6\u95f4")
    private long timestamp = Instant.now().toEpochMilli();
    @Schema(description="\u8bf7\u6c42\u6d88\u606f\u4f53")
    private final String version = CommonConstants.DEFAULT_KMC_VERSION;
    @Schema(description="\u8bf7\u6c42id")
    private String requestId;
    @Schema(description="\u8fd4\u56de\u6570\u636e")
    private T data;
    private static final long serialVersionUID = -3685249101751401211L;

    public static <T> Result<T> success(Code code, String message, T data) {
        return Result.of(StatusEnum.SUCCESS.name(), code.getCode(), message, data);
    }

    public static <T> Result<T> success(String message, T data) {
        return Result.of(StatusEnum.SUCCESS.name(), ResultEnum.SUCCESS.codeDesc(), message, data);
    }

    public static <T> Result<T> success(ResultEnum resultEnum, T data) {
        return Result.of(StatusEnum.SUCCESS.name(), resultEnum.codeDesc(), resultEnum.getDesc(), data);
    }

    public static <T> Result<T> success(ResultEnum resultEnum) {
        return Result.of(StatusEnum.SUCCESS.name(), resultEnum.codeDesc(), resultEnum.getDesc(), null);
    }

    public static <T> Result<T> success(T data) {
        return Result.of(StatusEnum.SUCCESS.name(), ResultEnum.SUCCESS.codeDesc(), ResultEnum.SUCCESS.getDesc(), data);
    }

    public static <T> Result<T> success() {
        return Result.of(StatusEnum.SUCCESS.name(), ResultEnum.SUCCESS.codeDesc(), ResultEnum.SUCCESS.getDesc(), null);
    }

    public static <T> Result<T> fail(Code code, String message, T data) {
        return Result.of(StatusEnum.FAILURE.name(), code.getCode(), message, data);
    }

    public static <T> Result<T> fail(Code code, String message) {
        return Result.of(StatusEnum.FAILURE.name(), code.getCode(), message, null);
    }

    public static <T> Result<T> fail(String message, T data) {
        return Result.of(StatusEnum.FAILURE.name(), ResultEnum.SUCCESS.codeDesc(), message, data);
    }

    public static <T> Result<T> fail(String message) {
        return Result.of(StatusEnum.FAILURE.name(), ResultEnum.SUCCESS.codeDesc(), message, null);
    }

    public static <T> Result<T> fail(ResultEnum resultEnum, T data) {
        return Result.of(StatusEnum.FAILURE.name(), resultEnum.codeDesc(), resultEnum.getDesc(), data);
    }

    public static <T> Result<T> fail(ResultEnum resultEnum) {
        return Result.of(StatusEnum.FAILURE.name(), resultEnum.codeDesc(), resultEnum.getDesc(), null);
    }

    public static <T> Result<T> fail(T data) {
        return Result.of(StatusEnum.FAILURE.name(), ResultEnum.FAILED.codeDesc(), ResultEnum.FAILED.getDesc(), data);
    }

    public static <T> Result<T> fail(Exception exception) {
        return Result.of(StatusEnum.FAILURE.name(), ResultEnum.FAILED.codeDesc(), exception.getMessage(), null);
    }

    public static <T> Result<T> fail(Throwable throwable) {
        return Result.of(StatusEnum.FAILURE.name(), ResultEnum.FAILED.codeDesc(), throwable.getMessage(), null);
    }

    public static <T> Result<T> fail() {
        return Result.of(StatusEnum.FAILURE.name(), ResultEnum.FAILED.codeDesc(), ResultEnum.FAILED.getDesc(), null);
    }

    public static <T> Result<T> of(StatusEnum status, ResultEnum resultEnum, T data) {
        return Result.of(status.name(), resultEnum.codeDesc(), resultEnum.getDesc(), data);
    }

    public static <T> Result<T> of(StatusEnum status, Code code, String message, T data) {
        return Result.of(status.name(), code.getCode(), message, data);
    }

    public static <T> Result<T> of(String status, String code, String message, T data) {
        return (Result<T>) Result.builder().status(status).code(code).data(data).message(message).timestamp(Instant.now().toEpochMilli()).requestId(StrUtil.isNotBlank((CharSequence)TraceUtils.getKmcTraceId()) ? TraceUtils.getKmcTraceId() : IdGeneratorUtils.getIdStr()).build();
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getVersion() {
        return this.version;
    }

    public String getRequestId() {
        return this.requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> ResultBuilder<T> builder() {
        return new ResultBuilder();
    }

    public static final class ResultBuilder<T> {
        private String status;
        private String code;
        private String message;
        private long timestamp;
        private String requestId;
        private T data;

        private ResultBuilder() {
        }

        public ResultBuilder<T> status(String status) {
            this.status = status;
            return this;
        }

        public ResultBuilder<T> code(String code) {
            this.code = code;
            return this;
        }

        public ResultBuilder<T> message(String message) {
            this.message = message;
            return this;
        }

        public ResultBuilder<T> timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ResultBuilder<T> requestId(String requestId) {
            this.requestId = requestId;
            return this;
        }

        public ResultBuilder<T> data(T data) {
            this.data = data;
            return this;
        }

        public Result<T> build() {
            Result<T> result = new Result<T>();
            result.setStatus(this.status);
            result.setCode(this.code);
            result.setMessage(this.message);
            result.setTimestamp(this.timestamp);
            result.setRequestId(this.requestId);
            result.setData(this.data);
            return result;
        }
    }
}

