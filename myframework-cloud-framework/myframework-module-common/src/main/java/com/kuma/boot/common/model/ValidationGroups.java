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

package com.kuma.boot.common.model;

import jakarta.validation.groups.Default;

/**
 * 校验分组-默认定义
 *
 * <p>
 * 校验分组需灵活使用，默认定义只可解决常规场景或作为示例参考
 *
 * <p>
 * 使用 {@link jakarta.validation.GroupSequence} 注解可指定分组校验顺序，同时还拥有短路能力
 *
 * @author kuma
 * @version 2022.05
 * @since 2022-05-11 10:19:22
 */
public class ValidationGroups {

    public interface Create extends Default {}

    public interface Query extends Default {}

    public interface Update extends Default {}

    public interface Delete extends Default {}
}
