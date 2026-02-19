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

package com.kuma.boot.data.mybatis.mybatisplus.interceptor;

import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;

/**
 * 议员拦截器
 *
 * @author kuma
 * @version 2022.09
 * @since 2022-09-08 14:59:16
 */
public class MpInterceptor {

    private InnerInterceptor innerInterceptor;

    private int sortNo = 0;

    public MpInterceptor(InnerInterceptor innerInterceptor, int sortNo) {
        this.innerInterceptor = innerInterceptor;
        this.sortNo = sortNo;
    }

    public MpInterceptor(InnerInterceptor innerInterceptor) {
        this.innerInterceptor = innerInterceptor;
    }

    public InnerInterceptor getInnerInterceptor() {
        return innerInterceptor;
    }

    public void setInnerInterceptor(InnerInterceptor innerInterceptor) {
        this.innerInterceptor = innerInterceptor;
    }

    public int getSortNo() {
        return sortNo;
    }

    public void setSortNo(int sortNo) {
        this.sortNo = sortNo;
    }
}
