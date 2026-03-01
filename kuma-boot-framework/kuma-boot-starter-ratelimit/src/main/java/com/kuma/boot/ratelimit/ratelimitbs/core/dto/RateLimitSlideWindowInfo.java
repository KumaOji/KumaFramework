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

package com.kuma.boot.ratelimit.ratelimitbs.core.dto;

import java.io.Serializable;
import java.util.List;

public class RateLimitSlideWindowInfo implements Serializable {

    /**
     * 初始化时间
     */
    private long initTime;

    /**
     * 窗口子列表
     */
    private List<com.kuma.boot.ratelimit.ratelimitbs.core.dto.RateLimitSlideWindowDto> windowList;

    public long getInitTime() {
        return initTime;
    }

    public void setInitTime(long initTime) {
        this.initTime = initTime;
    }

    public List<com.kuma.boot.ratelimit.ratelimitbs.core.dto.RateLimitSlideWindowDto> getWindowList() {
        return windowList;
    }

    public void setWindowList(List<com.kuma.boot.ratelimit.ratelimitbs.core.dto.RateLimitSlideWindowDto> windowList) {
        this.windowList = windowList;
    }

    @Override
    public String toString() {
        return "RateLimitSlideWindowInfo{" + "initTime=" + initTime + ", windowList=" + windowList + '}';
    }
}
