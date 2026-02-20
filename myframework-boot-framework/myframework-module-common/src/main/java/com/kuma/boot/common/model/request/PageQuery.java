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

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

/**
 * 查询参数
 *
 * @author kuma
 * @version 2022.03
 * @since 2022-03-28 11:24:11
 */
public class PageQuery implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    /** 当前第几页 */
    @Schema(
            description = "当前第几页，默认1",
            example = "1",
            type = "integer",
            requiredMode = RequiredMode.REQUIRED,
            defaultValue = "1")
    @NotNull(message = "当前页显示数量不能为空")
    @Min(value = 0, message = "当前页数不能小于0")
    @Max(value = Integer.MAX_VALUE)
    private Integer currentPage;

    /** 每页显示条数 */
    @Schema(
            description = "每页显示条数，默认10",
            example = "20",
            type = "integer",
            requiredMode = RequiredMode.REQUIRED,
            defaultValue = "20")
    @NotNull(message = "每页数据显示数量不能为空")
    @Min(value = 5, message = "每页显示条数最小为5条")
    @Max(value = 100, message = "每页显示条数最大为100条")
    private Integer pageSize;

    /** 排序字段 */
    @Schema(description = "排序字段")
    private String sort;

    /** 排序方式 asc/desc */
    @Schema(description = "排序方式 asc/desc")
    private String order;

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public PageQuery pageQuery() {
        return this;
    }
}
