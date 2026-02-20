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

package com.kuma.boot.spring.config;

import com.kuma.boot.common.constant.PageableConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 分页配置属性
 *
 * @author kuma
 */
@Data
@ConfigurationProperties("ballcat.pageable")
public class PageableProperties {

    private String pageParameterName;
    private String sizeParameterName;
    private String sortParameterName;
    private int maxPageSize;

    public PageableProperties() {
        this.pageParameterName = PageableConstants.DEFAULT_PAGE_PARAMETER;
        this.sizeParameterName = PageableConstants.DEFAULT_SIZE_PARAMETER;
        this.sortParameterName = PageableConstants.DEFAULT_SORT_PARAMETER;
        this.maxPageSize = PageableConstants.DEFAULT_MAX_PAGE_SIZE;
    }
}
