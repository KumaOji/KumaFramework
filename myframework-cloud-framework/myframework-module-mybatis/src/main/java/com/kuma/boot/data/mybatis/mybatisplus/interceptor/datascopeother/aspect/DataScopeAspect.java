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

package com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascopeother.aspect;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascopeother.annotation.DataScope;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascopeother.exception.NoDataException;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascopeother.model.DataScopeInfo;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascopeother.service.MarkService;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * DataScopeAspect
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
@Aspect
@Component
public class DataScopeAspect {

    @Autowired
    private MarkService dataScopeService;

    // 通过ThreadLocal记录权限相关的属性值
    public static ThreadLocal<DataScopeParam> threadLocal = new ThreadLocal<>();

    public static DataScopeParam getDataScopeParam() {
        return threadLocal.get();
    }

    // 方法切点
    @Pointcut(
            "@annotation(com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascopeother.annotation.DataScope)")
    public void methodPointCut() {
    }

    @After("methodPointCut()")
    public void clearThreadLocal() {
        threadLocal.remove();
        LogUtils.debug("threadLocal.remove()");
    }

    @Before("methodPointCut()")
    public void doBefore( JoinPoint point ) {
        Signature signature = point.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        // 获得注解
        DataScope dataScope = method.getAnnotation(DataScope.class);

        try {
            //            if (dataScope != null && !SecurityUtil.isAdmin()) {
            if (dataScope != null) {
                String scopeName = dataScope.value();
                DataScopeInfo dataScopeInfo = dataScopeService.execRuleByName(scopeName);

                DataScopeParam dataScopeParam = new DataScopeParam();

                dataScopeParam.setDataScopeInfo(dataScopeInfo);

                threadLocal.set(dataScopeParam);
            }
        } catch (NoDataException e) {
            throw new NoDataException(e.getMessage());
        } catch (Exception e) {
            LogUtils.error(e);
            throw new RuntimeException("数据权限 method 切面错误：" + e.getMessage());
        }
    }

    /**
     * DataScopeParam
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    public static class DataScopeParam {

        private DataScopeInfo dataScopeInfo;

        public DataScopeInfo getDataScopeInfo() {
            return dataScopeInfo;
        }

        public void setDataScopeInfo( DataScopeInfo dataScopeInfo ) {
            this.dataScopeInfo = dataScopeInfo;
        }
    }

    // 形参切点
    @Pointcut("execution(* *(.., @com.gitee.whzzone.common.annotation.DataScope (*), ..))")
    public void parameterPointCut() {
    }

    @Around("parameterPointCut()")
    public Object doAround( ProceedingJoinPoint point ) {
        try {
            Object[] args = point.getArgs();
            MethodSignature methodSignature = (MethodSignature) point.getSignature();
            Annotation[][] parameterAnnotations =
                    methodSignature.getMethod().getParameterAnnotations();
            // 遍历方法的参数注解和参数类型
            for (int i = 0; i < parameterAnnotations.length; i++) {
                Annotation[] annotations = parameterAnnotations[i];
                Class<?> parameterType = methodSignature.getParameterTypes()[i];

                int index = -1;

                for (int k = 0; k < annotations.length; k++) {
                    if (annotations[k] instanceof DataScope) {
                        index = k;
                        break;
                    }
                }

                if (index >= 0) {
                    if (parameterType != DataScopeInfo.class) {
                        throw new RuntimeException("使用@DataScope的参数类型必须是：DataScopeInfo.class 类型");
                    }
                    DataScope dataScope = (DataScope) annotations[index];
                    String scopeName = dataScope.value();
                    DataScopeInfo dataScopeInfo = dataScopeService.execRuleByName(scopeName);
                    args[i] = dataScopeInfo;
                }
            }

            // 继续执行目标方法
            return point.proceed(args);

        } catch (NoDataException e) {
            throw new NoDataException();
        } catch (Exception e) {
            LogUtils.error(e);
            throw new RuntimeException("数据权限 形参 切面错误：" + e.getMessage());
        } catch (Throwable e) {
            LogUtils.error(e);
            throw new RuntimeException(e);
        }
    }
}
