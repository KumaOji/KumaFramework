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

package com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.perm.handler;

import com.kuma.boot.common.holder.UserContextHolder;
import com.kuma.boot.core.utils.aop.AopUtils;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.perm.NestedPermission;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.perm.Permission;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.perm.local.DataPermContextHolder;
import java.util.Objects;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/** 忽略权限控制切面处理类 */
@Aspect
@Component
public class DataPermAspectHandler {

    /** 数据权限注解切面 */
    @Around("@annotation(permission)||@within(permission)")
    public Object doAround(ProceedingJoinPoint pjp, Permission permission) throws Throwable {
        Object obj = null;
        // 如果方法和类同时存在, 以方法上的注解为准
        Permission methodAnnotation = AopUtils.getAnnotation(pjp, Permission.class);
        if (Objects.nonNull(methodAnnotation)) {
            DataPermContextHolder.putPermission(methodAnnotation);
        } else {
            DataPermContextHolder.putPermission(permission);
        }

        DataPermContextHolder.putUserDetail(UserContextHolder.getUser());
        try {
            obj = pjp.proceed();
        } finally {
            DataPermContextHolder.clearUserAndPermission();
        }
        return obj;
    }

    @Around("@annotation(nestedPermission)||@within(nestedPermission)")
    public Object doAround(ProceedingJoinPoint pjp, NestedPermission nestedPermission)
            throws Throwable {
        Object obj = null;
        // 如果方法和类同时存在, 以方法上的注解为准
        NestedPermission methodAnnotation = AopUtils.getAnnotation(pjp, NestedPermission.class);
        if (Objects.nonNull(methodAnnotation)) {
            DataPermContextHolder.putNestedPermission(methodAnnotation);
        } else {
            DataPermContextHolder.putNestedPermission(nestedPermission);
        }

        DataPermContextHolder.putUserDetail(UserContextHolder.getUser());
        try {
            obj = pjp.proceed();
        } finally {
            DataPermContextHolder.clearNestedPermission();
        }
        return obj;
    }
}
