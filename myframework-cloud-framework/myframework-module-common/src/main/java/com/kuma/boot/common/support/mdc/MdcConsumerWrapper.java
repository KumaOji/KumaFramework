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

import java.util.function.Consumer;

/** <b>{@linkplain Consumer} 包装类（支持 {@linkplain org.slf4j.MDC MDC} 调用链跟踪）</b> */
public class MdcConsumerWrapper<T> extends MdcConsumer<T> {

    private Consumer<T> c;

    public MdcConsumerWrapper(Consumer<T> c) {
        this.c = c;
    }

    public MdcConsumerWrapper(Consumer<T> c, MdcAttr mdcAttr) {
        super(mdcAttr);
        this.c = c;
    }

    @Override
    public void doAccept(T t) {
        c.accept(t);
    }

    public static <T> MdcConsumerWrapper<T> of(Consumer<T> c) {
        return new MdcConsumerWrapper<T>(c);
    }

    public Consumer<T> getC() {
        return c;
    }
}
