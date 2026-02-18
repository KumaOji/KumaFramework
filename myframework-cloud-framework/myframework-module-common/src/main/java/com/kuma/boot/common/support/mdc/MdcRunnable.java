/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

/** <b>{@linkplain Runnable} 基类（支持 {@linkplain org.slf4j.MDC MDC} 调用链跟踪）</b> */
public abstract class MdcRunnable implements Runnable {

    private final MdcAttr mdcAttr;

    protected abstract void doRun();

    public MdcRunnable() {
        this(MdcAttr.fromMdc());
    }

    public MdcRunnable(MdcAttr mdcAttr) {
        this.mdcAttr = mdcAttr;
    }

    @Override
    public void run() {
        try {
            mdcAttr.putMdc();

            doRun();
        } finally {
            mdcAttr.removeMdc();
        }
    }

    public MdcAttr getMdcAttr() {
        return mdcAttr;
    }
}
