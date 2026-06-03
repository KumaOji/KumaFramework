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

package com.kuma.boot.dingtalk.autoconfigure.properties;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.dingtalk.enums.DingerType;
import com.kuma.boot.dingtalk.exception.InvalidPropertiesFormatException;
import com.kuma.boot.dingtalk.utils.ConfigTools;
import com.kuma.boot.dingtalk.utils.DingerUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 属性配置类
 *
 * @author kuma
 * @version 2022.07
 * @since 2022-07-06 15:24:30
 */
@RefreshScope
@ConfigurationProperties(prefix = DingtalkProperties.PREFIX)
public class DingtalkProperties implements InitializingBean {

    public static final String PREFIX = "kuma.boot.dingtalk";

    /** 是否启用DingTalk, 默认true, 选填 */
    private boolean enabled = false;

    /** dinger类型 <code>key={@link DingerType}, value={@link Dinger}</code>, 必填 */
    private Map<DingerType, Dinger> dingers = new LinkedHashMap<>();

    /** 项目名称, 必填 <code>eg: ${spring.application.name}</code> */
    private String projectId;

    /**
     * dinger xml配置路径(需要配置xml方式Dinger时必填), 选填
     *
     * <blockquote>
     *
     * spring.dinger.dinger-locations: classpath*:dinger/*.xml spring.dinger.dinger-locations:
     * classpath*:dinger/*\/*.xml
     *
     * </blockquote>
     */
    private String dingerLocations;

    /** 默认的Dinger, 不指定则使用{@link DingtalkProperties#dingers}中的第一个, 选填 */
    private DingerType defaultDinger = DingerType.DINGTALK;

    private DingtalkHttpClient httpclient = new DingtalkHttpClient();

    private DingtalkThreadPool threadPool = new DingtalkThreadPool();


    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Map<DingerType, Dinger> getDingers() {
        return dingers;
    }

    public void setDingers(Map<DingerType, Dinger> dingers) {
        this.dingers = dingers;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getDingerLocations() {
        return dingerLocations;
    }

    public void setDingerLocations(String dingerLocations) {
        this.dingerLocations = dingerLocations;
    }

    public DingerType getDefaultDinger() {
        return defaultDinger;
    }

    public void setDefaultDinger(DingerType defaultDinger) {
        this.defaultDinger = defaultDinger;
    }

    public static class Dinger {

        /** 请求地址前缀-选填 */
        private String robotUrl;

        /**
         * 获取 access_token, 必填
         *
         * <blockquote>
         *
         * 填写Dinger机器人设置中 webhook access_token | key后面的值 <br>
         * <br>
         *
         * <ul>
         *   <li>DingTalk：
         *       https://oapi.dingtalk.com/robot/send?access_token=c60d4824e0ba4a30544e81212256789331d68b0085ed1a5b2279715741355fbc
         *   <li>tokenId=c60d4824e0ba4a30544e81212256789331d68b0085ed1a5b2279715741355fbc
         *   <li>WeTalk：
         *       https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=20201220-7082-46d5-8a39-2ycy23b6df89
         *   <li>tokenId=20201220-7082-46d5-8a39-2ycy23b6df89
         * </ul>
         *
         * </blockquote>
         */
        private String tokenId;

        /** 选填, 签名秘钥。 需要验签时必填(钉钉机器人提供) */
        private String secret;

        /** 选填, 是否需要对tokenId进行解密, 默认false */
        private boolean decrypt = false;

        /**
         * 选填(当decrypt=true时, 必填), 解密密钥
         *
         * <p><br>
         * <br>
         * <b>解密密钥获取方式</b>
         *
         * <ul>
         *   <li>java -jar dinger-spring-boot-starter-[1.0.0].jar [tokenId]
         *   <li>ConfigTools.encrypt(tokenId)
         * </ul>
         */
        private String decryptKey;

        /** 选填, 是否开启异步处理, 默认： false */
        private boolean async = false;

        public String getRobotUrl() {
            return robotUrl;
        }

        public void setRobotUrl(String robotUrl) {
            this.robotUrl = robotUrl;
        }

        public String getTokenId() {
            return tokenId;
        }

        public void setTokenId(String tokenId) {
            this.tokenId = tokenId;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public boolean isDecrypt() {
            return decrypt;
        }

        public void setDecrypt(boolean decrypt) {
            this.decrypt = decrypt;
        }

        public String getDecryptKey() {
            return decryptKey;
        }

        public void setDecryptKey(String decryptKey) {
            this.decryptKey = decryptKey;
        }

        public boolean isAsync() {
            return async;
        }

        public void setAsync(boolean async) {
            this.async = async;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (dingers.isEmpty()) {
            throw new InvalidPropertiesFormatException("spring.dinger.dingers is empty.");
        }

        for (Map.Entry<DingerType, Dinger> entry : dingers.entrySet()) {
            DingerType dingerType = entry.getKey();
            if (!dingerType.isEnabled()) {
                throw new InvalidPropertiesFormatException(String.format("dinger=%s is disabled", dingerType));
            }

            Dinger dinger = entry.getValue();
            String tokenId = dinger.getTokenId();
            if (StringUtils.isEmpty(tokenId)) {
                throw new InvalidPropertiesFormatException("spring.dinger.token-id is empty.");
            }

            if (DingerUtils.isEmpty(dinger.robotUrl)) {
                dinger.robotUrl = dingerType.getRobotUrl();
            }

            if (dingerType == DingerType.WETALK) {
                dinger.secret = null;
            }

            boolean check = dinger.decrypt && StringUtils.isEmpty(dinger.decryptKey);
            if (check) {
                throw new InvalidPropertiesFormatException(
                        "spring.dinger.decrypt is true but spring.dinger.decrypt-key is empty.");
            }

            if (dinger.decrypt) {
                dinger.tokenId = ConfigTools.decrypt(dinger.decryptKey, dinger.tokenId);
            } else {
                dinger.decryptKey = null;
            }

            if (defaultDinger == null) {
                defaultDinger = dingerType;
                LogUtils.debug(
                        "defaultDinger undeclared and use first dingers dingerType," + " defaultDinger={}.",
                        defaultDinger);
            }
        }

        if (!defaultDinger.isEnabled()) {
            throw new InvalidPropertiesFormatException("spring.dinger.default-dinger is disabled.");
        }

        if (DingerUtils.isEmpty(this.projectId)) {
            throw new InvalidPropertiesFormatException("spring.dinger.project-id is empty.");
        }
    }


    public static class DingtalkHttpClient {

        /** 连接超时时间 */
        @DurationUnit(ChronoUnit.SECONDS)
        private Duration connectTimeout = Duration.ofSeconds(30);

        /** 读超时时间 */
        @DurationUnit(ChronoUnit.SECONDS)
        private Duration readTimeout = Duration.ofSeconds(30);

        public Duration getConnectTimeout() {
            return connectTimeout;
        }

        public void setConnectTimeout(Duration connectTimeout) {
            this.connectTimeout = connectTimeout;
        }

        public Duration getReadTimeout() {
            return readTimeout;
        }

        public void setReadTimeout(Duration readTimeout) {
            this.readTimeout = readTimeout;
        }
    }


    public static class DingtalkThreadPool {

        private static final int DEFAULT_CORE_SIZE = Runtime.getRuntime().availableProcessors() + 1;

        /** 线程池维护线程的最小数量, 选填 */
        private int coreSize = DEFAULT_CORE_SIZE;
        /** 线程池维护线程的最大数量, 选填 */
        private int maxSize = DEFAULT_CORE_SIZE * 2;
        /** 空闲线程的存活时间, 选填 */
        private int keepAliveSeconds = 60;
        /** 持有等待执行的任务队列, 选填 */
        private int queueCapacity = 10;
        /** 线程名称前缀, 选填 */
        private String threadNamePrefix = "ttc-dinger-threadpool-";

        public int getCoreSize() {
            return coreSize;
        }

        public void setCoreSize(int coreSize) {
            this.coreSize = coreSize;
        }

        public int getMaxSize() {
            return maxSize;
        }

        public void setMaxSize(int maxSize) {
            this.maxSize = maxSize;
        }

        public int getKeepAliveSeconds() {
            return keepAliveSeconds;
        }

        public void setKeepAliveSeconds(int keepAliveSeconds) {
            this.keepAliveSeconds = keepAliveSeconds;
        }

        public int getQueueCapacity() {
            return queueCapacity;
        }

        public void setQueueCapacity(int queueCapacity) {
            this.queueCapacity = queueCapacity;
        }

        public String getThreadNamePrefix() {
            return threadNamePrefix;
        }

        public void setThreadNamePrefix(String threadNamePrefix) {
            this.threadNamePrefix = threadNamePrefix;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public DingtalkHttpClient getHttpclient() {
        return httpclient;
    }

    public void setHttpclient(DingtalkHttpClient httpclient) {
        this.httpclient = httpclient;
    }

    public DingtalkThreadPool getThreadPool() {
        return threadPool;
    }

    public void setThreadPool(DingtalkThreadPool threadPool) {
        this.threadPool = threadPool;
    }
}
