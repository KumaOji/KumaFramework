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

package com.kuma.cloud.project1.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuma.boot.common.model.domain.PageParam;
import com.kuma.boot.common.model.result.PageResult;
import com.kuma.cloud.project1.entity.Source;

/**
 * 资源服务
 *
 * @author kuma
 */
public interface SourceService {

    /**
     * 分页查询资源列表
     *
     * @param pageParam 分页参数
     * @param name      资源名称（模糊查询，可选）
     * @return 分页结果
     */
    PageResult<Source> pageList(PageParam pageParam, String name);

    /**
     * 根据ID查询
     */
    Source getById(Long id);
}
