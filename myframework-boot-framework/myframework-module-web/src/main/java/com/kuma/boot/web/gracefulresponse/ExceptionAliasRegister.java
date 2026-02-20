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

package com.kuma.boot.web.gracefulresponse;

import com.kuma.boot.web.gracefulresponse.api.ExceptionAliasFor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class ExceptionAliasRegister {

    private Logger logger = LoggerFactory.getLogger(ExceptionAliasRegister.class);

    private ConcurrentHashMap<Class<? extends Throwable>, ExceptionAliasFor> aliasForMap =
            new ConcurrentHashMap<>();

    /**
     * 注册
     *
     * @param throwableClass
     * @return
     */
    public ExceptionAliasRegister doRegisterExceptionAlias(
            Class<? extends Throwable> throwableClass) {

        ExceptionAliasFor exceptionAliasFor = throwableClass.getAnnotation(ExceptionAliasFor.class);
        if (exceptionAliasFor == null) {
            logger.error("注册了未加ExceptionAliasFor的异常,throwableClass={}", throwableClass);
            throw new RuntimeException();
        }

        Class<? extends Throwable>[] classes = exceptionAliasFor.aliasFor();
        for (Class<? extends Throwable> c : classes) {
            aliasForMap.put(c, exceptionAliasFor);
        }

        return this;
    }

    /**
     * 获取
     *
     * @param tClazz
     * @return
     */
    public ExceptionAliasFor getExceptionAliasFor(Class<? extends Throwable> tClazz) {
        return aliasForMap.get(tClazz);
    }
}
