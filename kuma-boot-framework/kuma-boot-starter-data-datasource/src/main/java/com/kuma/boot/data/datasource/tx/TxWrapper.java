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

package com.kuma.boot.data.datasource.tx;

import com.kuma.boot.common.utils.exception.ExceptionUtils;

import java.util.Objects;
import java.util.concurrent.Callable;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * TxWrapper
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class TxWrapper {

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void doInTransaction( Runnable runnable ) {

        Objects.requireNonNull(runnable, "runnable is null");

        runnable.run();
    }


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public <T> T doInTransaction( Callable<T> callable ) {

        Objects.requireNonNull(callable, "callable is null");

        try {
            return callable.call();
        } catch (Exception e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void doInNewTransaction( Runnable runnable ) {

        Objects.requireNonNull(runnable, "runnable is null");

        runnable.run();
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public <T> T doInNewTransaction( Callable<T> callable ) {

        Objects.requireNonNull(callable, "callable is null");

        try {
            return callable.call();
        } catch (Exception e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

}
