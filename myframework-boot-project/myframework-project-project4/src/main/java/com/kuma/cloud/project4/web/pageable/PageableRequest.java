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

package com.kuma.cloud.project4.web.pageable;

import com.kuma.boot.common.constant.PageableConstants;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;

import java.util.List;

/**
 * 分页查询入参（Swagger 展示用）
 * <p>将 PageParam 在 OpenAPI 中展开为扁平化的 page、size、sort 等参数
 *
 * @author kuma
 */
@ParameterObject
@Getter
@Setter
@Schema(title = "分页查询参数", description = "GET 请求 URL 参数，支持 sort 数组或 sorts[field]/sorts[asc] 格式")
public class PageableRequest {

    @Schema(description = "当前页码，从 1 开始", example = "1", defaultValue = "1", minimum = "1")
    @Min(value = 1, message = "当前页不能小于 1")
    private long page = 1;

    @Schema(description = "每页条数，最大 100", example = "10", defaultValue = "10", minimum = "1")
    @Min(value = 1, message = "每页条数不能小于 1")
    private long size = 10;

    @Schema(description = "排序规则，每项格式：field,asc|desc。多字段传多个 sort：sort=id,desc&sort=name,asc。或 sorts[0][field]=id&sorts[0][asc]=false&sorts[1][field]=name&sorts[1][asc]=true")
    @ArraySchema(schema = @Schema(type = "string", pattern = PageableConstants.SORT_REGEX, example = "id,desc"))
    private List<String> sort;
}
