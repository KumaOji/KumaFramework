/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  io.swagger.v3.oas.annotations.media.Schema
 */
package com.kuma.boot.common.model.response;

import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.model.Code;
import com.kuma.boot.common.model.response.EmptyResponse;
import com.kuma.boot.common.model.response.ResponseBase;
import com.kuma.boot.common.utils.id.IdGeneratorUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.Instant;

@Schema(description="\u8fd4\u56de\u7ed3\u679c\u5bf9\u8c61")
public class Response<T extends ResponseBase>
implements Serializable {
    @Schema(description="\u72b6\u6001\u503c")
    private String status = StatusEnum.SUCCESS.name();
    @Schema(description="code\u7801")
    private String code = ResultEnum.SUCCESS.codeDesc();
    @Schema(description="\u6d88\u606f")
    private String message = CommonConstants.REQUEST_SUCCESS;
    @Schema(description="\u8bf7\u6c42\u7ed3\u675f\u65f6\u95f4")
    private long timestamp = Instant.now().toEpochMilli();
    @Schema(description="\u7248\u672c\u53f7")
    private final String version = CommonConstants.DEFAULT_KMC_VERSION;
    @Schema(description="\u8bf7\u6c42No")
    private String responseNo = IdGeneratorUtils.getIdStr();
    @Schema(description="\u8fd4\u56de\u6570\u636e")
    private T result;
    private static final long serialVersionUID = -3685249101751401211L;

    public static <T extends ResponseBase> Response<T> from(T data) {
        Response<T> response = new Response<T>();
        response.setResult(data);
        return response;
    }

    public static <T extends ResponseBase> Response<T> empty() {
        Response<EmptyResponse> response = new Response<EmptyResponse>();
        EmptyResponse emptyResponse = new EmptyResponse();
        response.setResult(emptyResponse);
        return (Response<T>) response;
    }

    public static Response<?> from(StatusEnum status, Code code) {
        Response response = Response.empty();
        response.setStatus(status.name());
        response.setCode(code.getCode());
        return response;
    }

    public static <T extends ResponseBase> Response<T> from(StatusEnum status, Code code, T data) {
        Response<T> response = new Response<T>();
        response.setStatus(status.name());
        response.setCode(code.getCode());
        response.setResult(data);
        return response;
    }

    public static <T extends ResponseBase> Response<T> from(StatusEnum status, Code code, T data, String message) {
        Response<T> response = new Response<T>();
        response.setStatus(status.name());
        response.setCode(code.getCode());
        response.setResult(data);
        response.setMessage(message);
        return response;
    }

    public static <T extends ResponseBase> Response<T> from(StatusEnum status, Code code, String message) {
        Response<T> response = new Response<T>();
        response.setStatus(status.name());
        response.setCode(code.getCode());
        response.setMessage(message);
        return response;
    }

    public static <T extends ResponseBase> Response<T> from(StatusEnum status, ResultEnum resultEnum, T data) {
        Response<T> response = new Response<T>();
        response.setStatus(status.name());
        response.setCode(resultEnum.code());
        response.setMessage(resultEnum.getDesc());
        response.setResult(data);
        return response;
    }

    public boolean hasResult() {
        return this.result != null && !(this.result instanceof EmptyResponse);
    }

    public boolean success() {
        return StatusEnum.SUCCESS.name().equals(this.status);
    }

    public boolean failure() {
        return StatusEnum.FAILURE.name().equals(this.status);
    }

    public boolean processing() {
        return StatusEnum.PROCESSING.name().equals(this.status);
    }

    public boolean unknown() {
        return StatusEnum.UNKNOWN.name().equals(this.status);
    }

    public void throwIfNotSuccess() {
        if (!this.success()) {
            throw new BusinessException(Code.raw(this.code), this.message);
        }
    }

    public void throwIfFailure() {
        if (this.failure()) {
            throw new BusinessException(Code.raw(this.code), this.message);
        }
    }

    public void throwIfProcessing() {
        if (this.processing()) {
            throw new BusinessException(Code.raw(this.code), this.message);
        }
    }

    public void throwIfUnknown() {
        if (this.unknown()) {
            throw new BusinessException(Code.raw(this.code), this.message);
        }
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status.name();
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(Code code) {
        this.code = code.getCode();
    }

    public void setCode(ResultEnum resultEnum) {
        this.code = resultEnum.codeDesc();
    }

    public T getResult() {
        return this.result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getVersion() {
        return this.version;
    }

    public String getResponseNo() {
        return this.responseNo;
    }

    public void setResponseNo(String responseNo) {
        this.responseNo = responseNo;
    }
}

