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

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serial;
import java.util.List;

/**
 * 返回分页实体类
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 19:09:19
 */
@Schema(description = "分页结果对象")
public class PageResponse<T> extends ResponseBase {

    @Serial
    private static final long serialVersionUID = -3685249101751401211L;

    @Schema(description = "结果对象")
    private List<T> data;

    public static <T> PageResponse<T> from(List<T> data)  {
        PageResponse<T> batchResponse = new PageResponse<>();
        batchResponse.setData(data);
        return batchResponse;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
