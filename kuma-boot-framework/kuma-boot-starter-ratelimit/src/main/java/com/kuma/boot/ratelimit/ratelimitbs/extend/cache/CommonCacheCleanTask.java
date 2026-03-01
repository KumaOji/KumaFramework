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

package com.kuma.boot.ratelimit.ratelimitbs.extend.cache;

import com.kuma.boot.common.utils.common.ArgUtils;
import com.kuma.boot.common.utils.log.LogUtils;

import java.util.Map;

public class CommonCacheCleanTask implements Runnable {

    private final Map<String, CommonCacheValueDto> map;

    public CommonCacheCleanTask(Map<String, CommonCacheValueDto> map) {
        ArgUtils.notNull(map, "map");
        this.map = map;
    }

    @Override
    public void run() {
        LogUtils.info("[Cache] 开始清理过期数据");

        // 当前时间固定，不需要考虑删除的耗时
        // 毕竟最多相差 1s，但是和系统的时钟交互是比删除耗时多的。
        long currentMills = System.currentTimeMillis();

        // 这种简单的实现存在一个问题，如果数量很多的时候，会比较慢。
        for (Map.Entry<String, CommonCacheValueDto> entry : map.entrySet()) {
            Long expireTime = entry.getValue().getExpireTime();
            if (expireTime == null) {
                continue;
            }

            if (currentMills >= expireTime) {
                final String key = entry.getKey();
                map.remove(key);
                LogUtils.info("[Cache] 移除 key: {}", key);
            }
        }

        LogUtils.info("[Cache] 结束清理过期数据");
    }
}
