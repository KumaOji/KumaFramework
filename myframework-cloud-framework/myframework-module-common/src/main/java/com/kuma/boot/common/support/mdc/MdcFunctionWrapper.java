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

package com.kuma.boot.common.support.mdc;

import java.util.function.Function;

/** <b>{@linkplain Function} 包装类（支持 {@linkplain org.slf4j.MDC MDC} 调用链跟踪）</b> */
public class MdcFunctionWrapper<T, R> extends MdcFunction<T, R> {

    private Function<T, R> f;

    public MdcFunctionWrapper(Function<T, R> f) {
        this.f = f;
    }

    public MdcFunctionWrapper(Function<T, R> f, MdcAttr mdcAttr) {
        super(mdcAttr);
        this.f = f;
    }

    @Override
    public R doApply(T t) {
        return f.apply(t);
    }

    public static <T, R> MdcFunctionWrapper<T, R> of(Function<T, R> f) {
        return new MdcFunctionWrapper<T, R>(f);
    }

    public Function<T, R> getF() {
        return f;
    }
}
