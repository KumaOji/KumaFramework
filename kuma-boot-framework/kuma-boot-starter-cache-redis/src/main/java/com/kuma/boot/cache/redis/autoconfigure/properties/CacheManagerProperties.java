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

package com.kuma.boot.cache.redis.autoconfigure.properties;

import com.kuma.boot.cache.redis.enums.CacheType;
import com.kuma.boot.cache.redis.enums.SerializerType;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Role;

import java.time.Duration;
import java.util.Map;

/**
 * CustomCacheProperties
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-07 21:15:21
 */
@ConfigurationProperties(prefix = CacheManagerProperties.PREFIX)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class CacheManagerProperties {

    public static final String PREFIX = "kuma.boot.cache.redis.cache-manager";

    private boolean enabled = true;

    /**
     * 鐩墠鍙敮鎸?REDIS 鍜?CAFFEINE 锛?CAFFEINE 鍙敤浜庨」鐩殑寮€鍙戠幆澧冩垨鑰呮紨绀虹幆澧冧娇鐢紝 鐢熶骇鐜璇风敤redis锛侊紒锛?
     */
    private CacheType type = CacheType.REDIS;
    /**
     * 搴忓垪鍖栫被鍨?
     */
    private SerializerType serializerType = SerializerType.JACK_SON;
    /**
     * 鏄惁缂撳瓨 null 鍊?
     */
    private Boolean cacheNullVal = true;

    /**
     * 閫氳繃 @Cacheable 娉ㄨВ鏍囨敞鐨勬柟娉曠殑缂撳瓨绛栫暐
     */
    private Cache def = new Cache();
    /**
     * 閽堝鏌愬嚑涓叿浣撶殑key鐗规畩閰嶇疆
     *
     * <p>鏀瑰睘鎬у彧瀵?redis 鏈夋晥锛侊紒锛?configs鐨刱ey闇€瑕侀厤缃垚@Cacheable娉ㄨВ鐨剉alue
     */
    private Map<String, Cache> configs;

    /**
     * stream
     */
    private Stream stream = new Stream();

    /**
     * Stream
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    public static class Stream {

        public static final String PREFIX = CacheManagerProperties.PREFIX + ".stream";
        /**
         * 鏄惁寮€鍚?stream
         */
        boolean enable = false;
        /**
         * consumer group锛岄粯璁わ細鏈嶅姟鍚?+ 鐜
         */
        String consumerGroup;
        /**
         * 娑堣垂鑰呭悕绉帮紝榛樿锛歩p + 绔彛
         */
        String consumerName;
        /**
         * poll 鎵归噺澶у皬
         */
        Integer pollBatchSize;
        /**
         * poll 瓒呮椂鏃堕棿
         */
        Duration pollTimeout;

        public boolean isEnable() {
            return enable;
        }

        public void setEnable( boolean enable ) {
            this.enable = enable;
        }

        public String getConsumerGroup() {
            return consumerGroup;
        }

        public void setConsumerGroup( String consumerGroup ) {
            this.consumerGroup = consumerGroup;
        }

        public String getConsumerName() {
            return consumerName;
        }

        public void setConsumerName( String consumerName ) {
            this.consumerName = consumerName;
        }

        public Integer getPollBatchSize() {
            return pollBatchSize;
        }

        public void setPollBatchSize( Integer pollBatchSize ) {
            this.pollBatchSize = pollBatchSize;
        }

        public Duration getPollTimeout() {
            return pollTimeout;
        }

        public void setPollTimeout( Duration pollTimeout ) {
            this.pollTimeout = pollTimeout;
        }
    }

    /**
     * Cache
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    public static class Cache {

        /**
         * key 鐨勮繃鏈熸椂闂?榛樿1澶╄繃鏈?eg: timeToLive: 1d
         */
        private Duration timeToLive = Duration.ofDays(1);

        /**
         * 鏄惁鍏佽缂撳瓨null鍊?
         */
        private boolean cacheNullValues = true;

        /**
         * key 鐨勫墠缂€ 鏈€鍚庣殑key鏍煎紡锛?keyPrefix + @Cacheable.value + @Cacheable.key
         *
         * <p>浣跨敤鍦烘櫙锛?寮€鍙?娴嬭瘯鐜 鎴栬€?婕旂ず/鐢熶骇 鐜锛屼负浜嗚妭鐪佽祫婧愶紝寰€寰€浼氬叡鐢ㄤ竴涓猺edis锛屽嵆鍙互鏍规嵁key鍓嶇紑鏉ュ尯鍒?褰撶劧锛屼篃鑳界敤鍒囨崲 database 鏉ュ疄鐜?
         */
        private String keyPrefix;

        /**
         * 鍐欏叆redis鏃讹紝鏄惁浣跨敤key鍓嶇紑
         */
        private boolean useKeyPrefix = true;

        /**
         * Caffeine 鐨勬渶澶х紦瀛樹釜鏁?
         */
        private int maxSize = 1000;

        public Duration getTimeToLive() {
            return timeToLive;
        }

        public void setTimeToLive( Duration timeToLive ) {
            this.timeToLive = timeToLive;
        }

        public boolean isCacheNullValues() {
            return cacheNullValues;
        }

        public void setCacheNullValues( boolean cacheNullValues ) {
            this.cacheNullValues = cacheNullValues;
        }

        public String getKeyPrefix() {
            return keyPrefix;
        }

        public void setKeyPrefix( String keyPrefix ) {
            this.keyPrefix = keyPrefix;
        }

        public boolean isUseKeyPrefix() {
            return useKeyPrefix;
        }

        public void setUseKeyPrefix( boolean useKeyPrefix ) {
            this.useKeyPrefix = useKeyPrefix;
        }

        public int getMaxSize() {
            return maxSize;
        }

        public void setMaxSize( int maxSize ) {
            this.maxSize = maxSize;
        }
    }

    public CacheType getType() {
        return type;
    }

    public void setType( CacheType type ) {
        this.type = type;
    }

    public SerializerType getSerializerType() {
        return serializerType;
    }

    public void setSerializerType( SerializerType serializerType ) {
        this.serializerType = serializerType;
    }

    public Boolean getCacheNullVal() {
        return cacheNullVal;
    }

    public void setCacheNullVal( Boolean cacheNullVal ) {
        this.cacheNullVal = cacheNullVal;
    }

    public Cache getDef() {
        return def;
    }

    public void setDef( Cache def ) {
        this.def = def;
    }

    public Map<String, Cache> getConfigs() {
        return configs;
    }

    public void setConfigs( Map<String, Cache> configs ) {
        this.configs = configs;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Stream getStream() {
        return stream;
    }

    public void setStream( Stream stream ) {
        this.stream = stream;
    }
}
