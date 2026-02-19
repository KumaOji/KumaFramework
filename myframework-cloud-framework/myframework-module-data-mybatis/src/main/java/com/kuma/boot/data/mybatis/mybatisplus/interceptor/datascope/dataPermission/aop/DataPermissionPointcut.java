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

package com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.dataPermission.aop;

import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.dataPermission.annotation.DataPermission;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;

/**
 * 自定义Pointcut
 *
 * @version 1.0
 * @since 2022/9/10 11:09
 */
public class DataPermissionPointcut {

    /** 自定义切入点 复合切点，为创建多个切点而提供的方便操作类。它所有的方法都返回ComposablePointcut类，这样，我们就可以使用链接表达式对其进行操作。 */
    protected static Pointcut of() {
        Pointcut classPointcut = new AnnotationMatchingPointcut(DataPermission.class, true);
        Pointcut methodPointcut = new AnnotationMatchingPointcut(null, DataPermission.class, true);
        return new ComposablePointcut(classPointcut).union(methodPointcut);
    }

    private DataPermissionPointcut() {}
}
