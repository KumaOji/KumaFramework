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
 * Comparison
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class Comparison implements Serializable {

    // 字段
    private String Field;
    // 字段旧值
    private Object before;
    // 字段新值
    private Object after;

    public String getField() {
        return Field;
    }

    public void setField( String field ) {
        Field = field;
    }

    public Object getBefore() {
        return before;
    }

    public void setBefore( Object before ) {
        this.before = before;
    }

    public Object getAfter() {
        return after;
    }

    public void setAfter( Object after ) {
        this.after = after;
    }
}
