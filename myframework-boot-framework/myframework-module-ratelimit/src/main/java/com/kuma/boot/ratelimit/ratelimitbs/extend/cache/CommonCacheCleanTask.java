/*
 *  com.kuma.boot.common.utils.common.ArgUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 */
package com.kuma.boot.ratelimit.ratelimitbs.extend.cache;

import com.kuma.boot.common.utils.common.ArgUtils;
import com.kuma.boot.common.utils.log.LogUtils;

import java.util.Map;

public class CommonCacheCleanTask
implements Runnable {
    private final Map<String, CommonCacheValueDto> map;

    public CommonCacheCleanTask(Map<String, CommonCacheValueDto> map) {
        ArgUtils.notNull(map, (String)"map");
        this.map = map;
    }

    @Override
    public void run() {
        LogUtils.info((String)"[Cache] \u5f00\u59cb\u6e05\u7406\u8fc7\u671f\u6570\u636e", (Object[])new Object[0]);
        long currentMills = System.currentTimeMillis();
        for (Map.Entry<String, CommonCacheValueDto> entry : this.map.entrySet()) {
            Long expireTime = entry.getValue().getExpireTime();
            if (expireTime == null || currentMills < expireTime) continue;
            String key = entry.getKey();
            this.map.remove(key);
            LogUtils.info((String)"[Cache] \u79fb\u9664 key: {}", (Object[])new Object[]{key});
        }
        LogUtils.info((String)"[Cache] \u7ed3\u675f\u6e05\u7406\u8fc7\u671f\u6570\u636e", (Object[])new Object[0]);
    }
}

