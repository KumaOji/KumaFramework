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

package com.kuma.cloud.project1.model;

import java.util.LinkedHashMap;

/**
 * 动态表查询结果行 - 包装为 Map，避免 MyBatis 对 Map 返回类型要求 @MapKey
 *
 * @author kuma
 */
public class TableRow extends LinkedHashMap<String, Object> {

    private static final long serialVersionUID = 1L;
}
