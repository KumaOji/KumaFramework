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

import com.google.common.collect.Lists;
import com.kuma.boot.core.utils.reflect.AnnotationUtils;
import io.github.itning.retry.Retryer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * getCurrentMethod：获取方法信息即要做分片的批量调用的接口。 isHandler1：判断是否要做分片处理，只有第一参数是list并且list 的值大于1时才做分片处理。
 * around：具体分片逻辑。
 * <p>
 * 获取要分片方法的参数。 判断是否要做分片处理。 获取方法。 获取重试次数、重试间隔时间和分片大小。 生成重试器。 根据设置的分片大小，做分片处理。 调用批量接口并处理结果。
 * <p>
 * <p>
 * 只要在需要做分片的批量接口方法上，加上MethodPartAndRetryer注解就可以，重试次数、 重试间隔时间和分片大小可以在注解时设置，也可以使用默认值。
 *
 * @MethodPartAndRetryer(parts=100) public Boolean writeBackOfGoodsSN(List<SerialDTO>
 * listSerial,ObCheckWorker workerData)
 */
public class RetryAspectAop {

    @SuppressWarnings("unchecked")
    public Object around(final ProceedingJoinPoint point) throws Throwable {
        Object result = null;
        final Object[] args = point.getArgs();
        boolean isHandler1 = isHandler(args);
        if (isHandler1) {
            String className = point.getSignature().getDeclaringTypeName();
            String methodName = point.getSignature().getName();
            Object firstArg = args[0];
            List<Object> paramList = (List<Object>) firstArg;
            // 获取方法信息
            Method method = getCurrentMethod(point);
            // 获取注解信息
            com.kuma.boot.web.support.methodPartAndRetryer.MethodPartAndRetryer retryable =
                    AnnotationUtils.getAnnotation(method, com.kuma.boot.web.support.methodPartAndRetryer.MethodPartAndRetryer.class);
            // 重试机制
            Retryer<Object> retryer =
                    new RetryUtil<Object>()
                            .getDefaultRetryer(retryable.times(), retryable.waitTime());
            // 分片
            List<List<Object>> requestList = Lists.partition(paramList, retryable.parts());
            for (List<Object> partList : requestList) {
                args[0] = partList;
                Object tempResult =
                        retryer.call(
                                new Callable<Object>() {
                                    @Override
                                    public Object call() throws Exception {
                                        try {
                                            return point.proceed(args);
                                        } catch (Throwable throwable) {
                                            //							log.error(String.format("分片重试报错,类%s-方法%s",
                                            // className, methodName), throwable);
                                            throw new RuntimeException("分片重试出错");
                                        }
                                    }
                                });
                if (null != tempResult) {
                    if (tempResult instanceof Boolean) {
                        if (!((Boolean) tempResult)) {
                            //							log.error(String.format("分片执行报错返回类型不能转化bolean,类%s-方法%s",
                            // className, methodName));
                            throw new RuntimeException("分片执行报错!");
                        }
                        result = tempResult;
                    } else if (tempResult instanceof List) {
                        if (result == null) {
                            result = Lists.newArrayList();
                        }
                        ((List<Object>) result).addAll((List<Object>) tempResult);
                    } else {
                        //						log.error(String.format("分片执行返回的类型不支持,类%s-方法%s", className,
                        // methodName));
                        throw new RuntimeException("不支持该返回类型");
                    }
                } else {
                    //					log.error(String.format("分片执行返回的结果为空,类%s-方法%s", className, methodName));
                    throw new RuntimeException("调用结果为空");
                }
            }
        } else {
            result = point.proceed(args);
        }
        return result;
    }

    private boolean isHandler(Object[] args) {
        boolean isHandler = false;
        if (null != args && args.length > 0) {
            Object firstArg = args[0];
            // 如果第一个参数是list 并且数量大于1
            if (firstArg instanceof List<?> list && list.size() > 1) {
                isHandler = true;
            }
        }
        return isHandler;
    }

    private Method getCurrentMethod(ProceedingJoinPoint point) {
        try {
            Signature sig = point.getSignature();
            MethodSignature msig = (MethodSignature) sig;
            Object target = point.getTarget();
            return target.getClass().getMethod(msig.getName(), msig.getParameterTypes());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
