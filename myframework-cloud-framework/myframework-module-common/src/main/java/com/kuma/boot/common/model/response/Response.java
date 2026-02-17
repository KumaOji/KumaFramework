/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.common.model.response;

import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.model.Code;
import com.kuma.boot.common.utils.id.IdGeneratorUtils;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * 返回实体类
 *
 * @author kuma
 * @version 2023.04
 * @since 2023-05-10 15:50:14
 */
@Schema(description = "返回结果对象")
public class Response<T extends ResponseBase> implements Serializable {

    /**
     * 状态码
     */
    @Schema(description = "状态值")
    private String status = StatusEnum.SUCCESS.name();

    /**
     * 状态码
     */
    @Schema(description = "code码")
    private String code = ResultEnum.SUCCESS.codeDesc();

    /**
     * 状态码
     */
    @Schema(description = "消息")
    private String message = CommonConstants.REQUEST_SUCCESS;

    /**
     * 请求结束时间
     */
    @Schema(description = "请求结束时间")
    private long timestamp = Instant.now().toEpochMilli();

    /**
     * 请求消息体
     */
    @Schema(description = "版本号")
    private final String version = CommonConstants.DEFAULT_KMC_VERSION;

    /**
     * 请求id
     */
    @Schema(description = "请求No")
    private String responseNo = IdGeneratorUtils.getIdStr();

    /**
     * 返回数据
     */
    @Schema(description = "返回数据")
    private T result;

    @Serial
    private static final long serialVersionUID = -3685249101751401211L;

    /**
     * 成功返回
     *
     * @param data 数据
     * @return 返回结果
     * @since 2021-09-02 19:15:21
     */
    public static <T extends ResponseBase> Response<T> from(T data) {
        Response<T> response = new Response<>();
        response.setResult(data);
        return response;
    }

    @SuppressWarnings("unchecked")
    public static <T extends ResponseBase> Response<T> empty() {
        Response<T> response = new Response<>();
        EmptyResponse emptyResponse = new EmptyResponse();
        response.setResult((T) emptyResponse);
        return response;
    }

    /**
     * 失败返回
     *
     * @return 返回结果
     * @since 2021-09-02 19:13:14
     */
    public static Response<?> from(StatusEnum status, Code code) {
        Response<?> response = empty();
        response.setStatus(status.name());
        response.setCode(code.getCode());
        return response;
    }

    /**
     * 失败返回
     *
     * @return 返回结果
     * @since 2021-09-02 19:13:14
     */
    public static <T extends ResponseBase> Response<T> from(StatusEnum status, Code code, T data) {
        Response<T> response = new Response<>();
        response.setStatus(status.name());
        response.setCode(code.getCode());
        response.setResult(data);
        return response;
    }

    /**
     * 失败返回
     *
     * @return 返回结果
     * @since 2021-09-02 19:13:14
     */
    public static <T extends ResponseBase> Response<T> from(StatusEnum status, Code code, T data, String message) {
        Response<T> response = new Response<>();
        response.setStatus(status.name());
        response.setCode(code.getCode());
        response.setResult(data);
        response.setMessage(message);
        return response;
    }

    public static <T extends ResponseBase> Response<T> from(StatusEnum status, Code code, String message) {
        Response<T> response = new Response<>();
        response.setStatus(status.name());
        response.setCode(code.getCode());
        response.setMessage(message);
        return response;
    }


    public static <T extends ResponseBase> Response<T> from(StatusEnum status, ResultEnum resultEnum, T data) {
        Response<T> response = new Response<>();
        response.setStatus(status.name());
        response.setCode(resultEnum.code());
        response.setMessage(resultEnum.getDesc());
        response.setResult(data);
        return response;
    }

    public boolean hasResult() {
        return result != null && !(result instanceof EmptyResponse);
    }

    public boolean success() {
        return StatusEnum.SUCCESS.name().equals(status);
    }

    public boolean failure() {
        return StatusEnum.FAILURE.name().equals(status);
    }

    public boolean processing() {
        return StatusEnum.PROCESSING.name().equals(status);
    }

    public boolean unknown() {
        return StatusEnum.UNKNOWN.name().equals(status);
    }

    public void throwIfNotSuccess() {
        if (!success()) {
            throw new BusinessException(Code.raw(this.code), this.message);
        }
    }

    public void throwIfFailure() {
        if (failure()) {
            throw new BusinessException(Code.raw(this.code), this.message);
        }
    }

    public void throwIfProcessing() {
        if (processing()) {
            throw new BusinessException(Code.raw(this.code), this.message);
        }
    }

    public void throwIfUnknown() {
        if (unknown()) {
            throw new BusinessException(Code.raw(this.code), this.message);
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status.name();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(Code code) {
        this.code = code.getCode();
    }

    public void setCode(ResultEnum resultEnum) {
        this.code = resultEnum.codeDesc();
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public long getTimestamp() {
        return timestamp;
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
        return version;
    }

    public String getResponseNo() {
        return responseNo;
    }

    public void setResponseNo(String responseNo) {
        this.responseNo = responseNo;
    }
}
