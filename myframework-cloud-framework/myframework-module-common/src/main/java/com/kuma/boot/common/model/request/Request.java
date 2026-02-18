/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.common.model.request;

import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.common.utils.id.IdGeneratorUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

/**
 * 返回实体类
 *
 * @author kuma
 * @version 2023.04
 * @since 2023-05-10 15:50:14
 */
@Schema(description = "返回结果对象")
public class Request<T extends RequestBase> implements Serializable {

    /**
     * 请求消息体
     */
    @Schema(description = "请求消息体")
    private final String version = CommonConstants.DEFAULT_KMC_VERSION;

    /**
     * 异常消息体
     */
    @Schema(description = "异常消息体")
    private final String requestNo = IdGeneratorUtils.getIdStr();

    /**
     * 返回数据
     */
    @Schema(description = "返回数据")
    @NotNull(message = "请求参数不能为空")
    private T order;

    @Serial private static final long serialVersionUID = -3685249101751401211L;

    public static <T extends RequestBase> Request<T> from(T order) {
        Request<T> request = new Request<T>();
        request.setOrder(order);
        return request;
    }

    public static <T extends RequestBase> Request<T> empty() {
        Request<T> request =  new Request<T>();
        EmptyRequest emptyRequest = new EmptyRequest();
        request.setOrder((T)emptyRequest);
        return request;
    }

    public String getVersion() {
        return version;
    }

    public String getRequestNo() {
        return requestNo;
    }

    public T getOrder() {
        return order;
    }

    public void setOrder(T order) {
        this.order = order;
    }

}
