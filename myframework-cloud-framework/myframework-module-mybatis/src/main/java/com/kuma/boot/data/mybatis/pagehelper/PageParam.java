/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.data.mybatis.pagehelper;

import java.io.Serializable;

/**
 * PageParam
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class PageParam<T> implements Serializable {

    private static final long serialVersionUID = -7916211163897873899L;
    private int pageNum = 1;
    private int pageSize = 10;
    // 条件参数
    private T param;
    // 排序字段
    private String orderBy;

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize( int pageSize ) {
        this.pageSize = pageSize;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum( int pageNum ) {
        this.pageNum = pageNum;
    }

    public T getParam() {
        return param;
    }

    public void setParam( T param ) {
        this.param = param;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy( String orderBy ) {
        // 需要注意sql注入
        this.orderBy = orderBy;
    }
}

// 分页结果类
