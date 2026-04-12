/*
 * Copyright (c) 2020-2030, Shuigedeng (981376577@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.webagg.controller;

import com.kuma.boot.web.annotation.InnerApi;

/**
 * 简单的实现了BaseController，为了获取注入 Service 和 实体类型
 *
 * <p>基类该类后，没有任何方法。 可以让业务Controller继承 SuperSimpleController 后，按需实现 *Controller 接口
 *
 * @author shuigedeng
 * @version 2021.9
 * @since 2021-09-02 21:15:37
 */
@InnerApi
public abstract class InnerController implements CommonController {

}
