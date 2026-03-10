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

package com.kuma.cloud.cache.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Cache 配置属性
 *
 * <pre>
 * kuma:
 *   cache:
 *     size: 1024
 *     evict: lru
 *     persist:
 *       type: none
 *       path: /tmp/cache
 * </pre>
 *
 * @author kuma
 * @since 2025.01
 */
@ConfigurationProperties(prefix = "kuma.cache")
public class CacheProperties {

    /** 缓存最大条目数，默认不限制 */
    private int size = Integer.MAX_VALUE;

    /**
     * 驱除策略：none / fifo / lru / lru-double-list-map /
     * lru-linked-hash-map / lru-2q / lru-2 / lfu / clock
     */
    private String evict = "lru";

    /** 持久化配置 */
    private Persist persist = new Persist();

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public String getEvict() { return evict; }
    public void setEvict(String evict) { this.evict = evict; }

    public Persist getPersist() { return persist; }
    public void setPersist(Persist persist) { this.persist = persist; }

    public static class Persist {

        /** 持久化类型：none / db-json / aof */
        private String type = "none";

        /** 持久化文件路径，type 为 db-json 或 aof 时有效 */
        private String path = "";

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
    }
}
