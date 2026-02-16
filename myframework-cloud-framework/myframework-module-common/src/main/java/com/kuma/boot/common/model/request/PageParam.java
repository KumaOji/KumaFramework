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

package com.kuma.boot.common.model.request;

import com.kuma.boot.common.constant.PageableConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页查询参数
 *
 * @author kuma
 */
@Data
@Schema(title = "分页查询参数")
@NoArgsConstructor
public class PageParam {

    public PageParam(long offset, long limit) {
        if (limit > 0 && offset >= 0) {
            this.current = (offset / limit) + 1;
            this.page = (offset / limit) + 1;
            this.size = limit;
        }
    }

    @Deprecated
    @Schema(title = "当前页码, 建议使用 page 参数", description = "从 1 开始", defaultValue = "1", example = "1")
    @Min(value = 1, message = "当前页不能小于 1")
    private long current = 1;

    @Schema(title = "当前页码", description = "从 1 开始", defaultValue = "1", example = "1")
    @Min(value = 1, message = "当前页不能小于 1")
    private long page = 1;

    @Schema(title = "每页显示条数", description = "最大值为系统设置，默认 100", defaultValue = "10")
    @Min(value = 1, message = "每页显示条数不能小于1")
    private long size = 10;

    @Schema(title = "排序规则")
    @Valid
    private List<Sort> sorts = new ArrayList<>();

    public int getStartRow() {
        return (int) ((this.page - 1) * size);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(title = "排序元素载体")
    public static class Sort {

        @Schema(title = "排序字段", example = "id")
        @Pattern(regexp = PageableConstants.SORT_FILED_REGEX, message = "排序字段格式非法")
        private String field;

        @Schema(title = "是否正序排序", example = "false")
        private boolean asc;
    }
}
