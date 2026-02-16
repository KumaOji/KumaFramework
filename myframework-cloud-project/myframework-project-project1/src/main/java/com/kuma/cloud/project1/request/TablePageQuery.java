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

package com.kuma.cloud.project1.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serializable;

/**
 * 动态表分页查询 - 传入表名
 *
 * @author kuma
 */
@Data
@Schema(description = "表分页查询")
public class TablePageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "表名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "表名只能包含字母、数字、下划线")
    @Schema(description = "表名", example = "source", requiredMode = Schema.RequiredMode.REQUIRED)
    private String tableName;

    @NotNull(message = "当前页不能为空")
    @Min(value = 0, message = "当前页不能小于0")
    @Max(value = Integer.MAX_VALUE)
    @Schema(description = "当前页码", example = "1", defaultValue = "1")
    private Integer currentPage = 1;

    @NotNull(message = "每页条数不能为空")
    @Min(value = 5, message = "每页条数最小为5")
    @Max(value = 100, message = "每页条数最大为100")
    @Schema(description = "每页显示条数", example = "10", defaultValue = "10")
    private Integer pageSize = 10;

    @Schema(description = "排序字段", example = "id")
    @Pattern(regexp = "^[a-zA-Z0-9_]*$", message = "排序字段只能包含字母、数字、下划线")
    private String sort;

    @Schema(description = "排序方式 asc/desc", example = "desc")
    private String order;
}
