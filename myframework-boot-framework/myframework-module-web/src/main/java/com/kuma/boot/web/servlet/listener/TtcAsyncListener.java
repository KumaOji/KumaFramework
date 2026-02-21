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

package com.kuma.boot.web.servlet.listener;

import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;

import java.io.IOException;

// actx.addListener(new MyAsyncListener());
public class KmcAsyncListener implements AsyncListener {

    // 异步调用完成时触发
    @Override
    public void onComplete(AsyncEvent event) throws IOException {
        // 省略....
    }

    // 异步调用出错时触发
    @Override
    public void onError(AsyncEvent event) throws IOException {
        // 省略....
    }

    // 异步调用开始触发
    @Override
    public void onStartAsync(AsyncEvent event) throws IOException {
        // 省略....
    }

    // 异步调用超时触发
    @Override
    public void onTimeout(AsyncEvent event) throws IOException {
        // 省略....
    }
}
