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

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 查询参数
 *
 * @author kuma
 * @version 2022.03
 * @since 2022-03-28 11:24:11
 */
public class IdsCommand implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    /** 当前第几页 */
    @Schema(description = "id列表", requiredMode = RequiredMode.REQUIRED)
    @NotEmpty (message = "id列表不能为空")
    @Size(max = 100, message = "id列表最大为{max}条")
    private List<Long> ids;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds( List<Long> ids ) {
        this.ids = ids;
    }
}
