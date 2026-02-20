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

package com.kuma.boot.data.mybatis.interceptor.easylog.common.utils.audit;

import java.io.Serializable;

/**
 * LoggingContent
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class LoggingContent implements Serializable {

    /**
     * 方法行为-比如add操作
     */
    private String methodBehavior;

    /**
     * 操作的内容
     */
    private Object data;

    public String getMethodBehavior() {
        return methodBehavior;
    }

    public void setMethodBehavior( String methodBehavior ) {
        this.methodBehavior = methodBehavior;
    }

    public Object getData() {
        return data;
    }

    public void setData( Object data ) {
        this.data = data;
    }
}
