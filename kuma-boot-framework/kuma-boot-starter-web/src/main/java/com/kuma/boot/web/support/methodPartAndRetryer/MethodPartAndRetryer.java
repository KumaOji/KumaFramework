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

package com.kuma.boot.web.support.methodPartAndRetryer;

import java.lang.annotation.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/**
 * 随着数据量的增长，发现系统在与其他系统交互时，批量接口会出现超时现象，发现原批量接口在实现时，
 * 没有做分片处理，当数据过大时或超过其他系统阈值时，就会出现错误。
 * 由于与其他系统交互比较多，一个一个接口做分片优化，改动量较大，所以考虑通过AOP解决此问题
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MethodPartAndRetryer {
    /**
     * 失败重试次数
     *
     * @return
     */
    int times() default 3;

    /**
     * 失败间隔执行时间 300毫秒
     *
     * @return
     */
    long waitTime() default 300L;

    /**
     * 分片大小
     *
     * @return
     */
    int parts() default 200;
}
