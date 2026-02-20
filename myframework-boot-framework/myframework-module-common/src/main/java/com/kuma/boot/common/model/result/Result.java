/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.common.model.result;

import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.model.Code;
import com.kuma.boot.common.utils.id.IdGeneratorUtils;
import com.kuma.boot.common.utils.servlet.TraceUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.hutool.core.util.StrUtil;

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
public class Result<T> implements Serializable {

    /**
     * 状态码
     */
    @Schema(description = "状态码")
    private String status = StatusEnum.SUCCESS.name();

    /**
     * 状态码
     */
    @Schema(description = "状态码")
    private String code = ResultEnum.SUCCESS.codeDesc();

    /**
     * 状态码
     */
    @Schema(description = "状态码")
    private String message = CommonConstants.REQUEST_SUCCESS;

    /**
     * 请求结束时间
     */
    @Schema(description = "请求结束时间")
    private long timestamp = Instant.now().toEpochMilli();


    /**
     * 请求消息体
     */
    @Schema(description = "请求消息体")
    private final String version = CommonConstants.DEFAULT_KMC_VERSION;

    /**
     * 请求id
     */
    @Schema(description = "请求id")
    private String requestId;

    /**
     * 返回数据
     */
    @Schema(description = "返回数据")
    private T data;


    @Serial
    private static final long serialVersionUID = -3685249101751401211L;


    public static <T> Result<T> success( Code code, String message, T data ) {
        return of(StatusEnum.SUCCESS.name(), code.getCode(), message, data);
    }

    public static <T> Result<T> success( String message, T data ) {
        return of(StatusEnum.SUCCESS.name(), ResultEnum.SUCCESS.codeDesc(), message, data);
    }

    public static <T> Result<T> success( ResultEnum resultEnum, T data ) {
        return of(StatusEnum.SUCCESS.name(), resultEnum.codeDesc(), resultEnum.getDesc(), data);
    }

    public static <T> Result<T> success( ResultEnum resultEnum ) {
        return of(StatusEnum.SUCCESS.name(), resultEnum.codeDesc(), resultEnum.getDesc(), null);
    }

    public static <T> Result<T> success( T data ) {
        return of(StatusEnum.SUCCESS.name(), ResultEnum.SUCCESS.codeDesc(), ResultEnum.SUCCESS.getDesc(), data);
    }

    public static <T> Result<T> success() {
        return of(StatusEnum.SUCCESS.name(), ResultEnum.SUCCESS.codeDesc(), ResultEnum.SUCCESS.getDesc(), null);
    }

    public static <T> Result<T> fail( Code code, String message, T data ) {
        return of(StatusEnum.FAILURE.name(), code.getCode(), message, data);
    }

    public static <T> Result<T> fail( Code code, String message ) {
        return of(StatusEnum.FAILURE.name(), code.getCode(), message, null);
    }

    public static <T> Result<T> fail( String message, T data ) {
        return of(StatusEnum.FAILURE.name(), ResultEnum.SUCCESS.codeDesc(), message, data);
    }

    public static <T> Result<T> fail( String message ) {
        return of(StatusEnum.FAILURE.name(), ResultEnum.SUCCESS.codeDesc(), message, null);
    }

    public static <T> Result<T> fail( ResultEnum resultEnum, T data ) {
        return of(StatusEnum.FAILURE.name(), resultEnum.codeDesc(), resultEnum.getDesc(), data);
    }

    public static <T> Result<T> fail( ResultEnum resultEnum ) {
        return of(StatusEnum.FAILURE.name(), resultEnum.codeDesc(), resultEnum.getDesc(), null);
    }

    public static <T> Result<T> fail( T data ) {
        return of(StatusEnum.FAILURE.name(), ResultEnum.FAILED.codeDesc(), ResultEnum.FAILED.getDesc(), data);
    }

    public static <T> Result<T> fail( Exception exception ) {
        return of(StatusEnum.FAILURE.name(), ResultEnum.FAILED.codeDesc(), exception.getMessage(), null);
    }

    public static <T> Result<T> fail( Throwable throwable ) {
        return of(StatusEnum.FAILURE.name(), ResultEnum.FAILED.codeDesc(), throwable.getMessage(), null);
    }

    public static <T> Result<T> fail() {
        return of(StatusEnum.FAILURE.name(), ResultEnum.FAILED.codeDesc(), ResultEnum.FAILED.getDesc(), null);
    }


    public static <T> Result<T> of( StatusEnum status, ResultEnum resultEnum, T data ) {
        return of(status.name(), resultEnum.codeDesc(), resultEnum.getDesc(), data);
    }

    public static <T> Result<T> of( StatusEnum status, Code code, String message, T data ) {
        return of(status.name(), code.getCode(), message, data);
    }

    /**
     * of
     *
     * @param code code
     * @param data data
     * @param <T> T
     * @return {@link Result }
     * @since 2021-09-02 19:12:35
     */
    public static <T> Result<T> of( String status, String code, String message, T data ) {
        return Result.<T>builder()
                .status(status)
                .code(code)
                .data(data)
                .message(message)
                .timestamp(Instant.now().toEpochMilli())
                .requestId(
                        StrUtil.isNotBlank(TraceUtils.getKmcTraceId())
                                ? TraceUtils.getKmcTraceId()
                                : IdGeneratorUtils.getIdStr())
                .build();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus( String status ) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode( String code ) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage( String message ) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp( long timestamp ) {
        this.timestamp = timestamp;
    }

    public String getVersion() {
        return version;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId( String requestId ) {
        this.requestId = requestId;
    }

    public T getData() {
        return data;
    }

    public void setData( T data ) {
        this.data = data;
    }

    public static <T> ResultBuilder<T> builder() {
        return new ResultBuilder<>();
    }


    /**
     * ResultBuilder
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-17 10:30:45
     */
    public static final class ResultBuilder<T> {

        private String status;
        private String code;
        private String message;
        private long timestamp;
        private String requestId;
        private T data;

        private ResultBuilder() {
        }

        public ResultBuilder<T> status( String status ) {
            this.status = status;
            return this;
        }

        public ResultBuilder<T> code( String code ) {
            this.code = code;
            return this;
        }

        public ResultBuilder<T> message( String message ) {
            this.message = message;
            return this;
        }

        public ResultBuilder<T> timestamp( long timestamp ) {
            this.timestamp = timestamp;
            return this;
        }

        public ResultBuilder<T> requestId( String requestId ) {
            this.requestId = requestId;
            return this;
        }

        public ResultBuilder<T> data( T data ) {
            this.data = data;
            return this;
        }

        public Result<T> build() {
            Result<T> result = new Result<>();
            result.setStatus(status);
            result.setCode(code);
            result.setMessage(message);
            result.setTimestamp(timestamp);
            result.setRequestId(requestId);
            result.setData(data);
            return result;
        }
    }
}
