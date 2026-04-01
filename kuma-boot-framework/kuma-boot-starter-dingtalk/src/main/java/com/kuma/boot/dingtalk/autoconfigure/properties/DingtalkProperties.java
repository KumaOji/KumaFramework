/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.boot.context.properties.ConfigurationProperties
 *  org.springframework.boot.convert.DurationUnit
 *  org.springframework.cloud.context.config.annotation.RefreshScope
 */
package com.kuma.boot.dingtalk.autoconfigure.properties;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.dingtalk.enums.DingerType;
import com.kuma.boot.dingtalk.exception.InvalidPropertiesFormatException;
import com.kuma.boot.dingtalk.utils.ConfigTools;
import com.kuma.boot.dingtalk.utils.DingerUtils;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@ConfigurationProperties(prefix="kuma.boot.dingtalk")
public class DingtalkProperties
implements InitializingBean {
    public static final String PREFIX = "kuma.boot.dingtalk";
    private boolean enabled = false;
    private Map<DingerType, Dinger> dingers = new LinkedHashMap<DingerType, Dinger>();
    private String projectId;
    private String dingerLocations;
    private DingerType defaultDinger = DingerType.DINGTALK;
    private DingtalkHttpClient httpclient = new DingtalkHttpClient();
    private DingtalkThreadPool threadPool = new DingtalkThreadPool();

    public boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Map<DingerType, Dinger> getDingers() {
        return this.dingers;
    }

    public void setDingers(Map<DingerType, Dinger> dingers) {
        this.dingers = dingers;
    }

    public String getProjectId() {
        return this.projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getDingerLocations() {
        return this.dingerLocations;
    }

    public void setDingerLocations(String dingerLocations) {
        this.dingerLocations = dingerLocations;
    }

    public DingerType getDefaultDinger() {
        return this.defaultDinger;
    }

    public void setDefaultDinger(DingerType defaultDinger) {
        this.defaultDinger = defaultDinger;
    }

    public void afterPropertiesSet() throws Exception {
        if (!this.enabled) {
            return;
        }
        if (this.dingers.isEmpty()) {
            throw new InvalidPropertiesFormatException("spring.dinger.dingers is empty.");
        }
        for (Map.Entry<DingerType, Dinger> entry : this.dingers.entrySet()) {
            boolean check;
            DingerType dingerType = entry.getKey();
            if (!dingerType.isEnabled()) {
                throw new InvalidPropertiesFormatException(String.format("dinger=%s is disabled", new Object[]{dingerType}));
            }
            Dinger dinger = entry.getValue();
            String tokenId = dinger.getTokenId();
            if (StringUtils.isEmpty((String)tokenId)) {
                throw new InvalidPropertiesFormatException("spring.dinger.token-id is empty.");
            }
            if (DingerUtils.isEmpty(dinger.robotUrl)) {
                dinger.robotUrl = dingerType.getRobotUrl();
            }
            if (dingerType == DingerType.WETALK) {
                dinger.secret = null;
            }
            boolean bl = check = dinger.decrypt && StringUtils.isEmpty((String)dinger.decryptKey);
            if (check) {
                throw new InvalidPropertiesFormatException("spring.dinger.decrypt is true but spring.dinger.decrypt-key is empty.");
            }
            if (dinger.decrypt) {
                dinger.tokenId = ConfigTools.decrypt(dinger.decryptKey, dinger.tokenId);
            } else {
                dinger.decryptKey = null;
            }
            if (this.defaultDinger != null) continue;
            this.defaultDinger = dingerType;
            LogUtils.debug((String)"defaultDinger undeclared and use first dingers dingerType, defaultDinger={}.", (Object[])new Object[]{this.defaultDinger});
        }
        if (!this.defaultDinger.isEnabled()) {
            throw new InvalidPropertiesFormatException("spring.dinger.default-dinger is disabled.");
        }
        if (DingerUtils.isEmpty(this.projectId)) {
            throw new InvalidPropertiesFormatException("spring.dinger.project-id is empty.");
        }
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public DingtalkHttpClient getHttpclient() {
        return this.httpclient;
    }

    public void setHttpclient(DingtalkHttpClient httpclient) {
        this.httpclient = httpclient;
    }

    public DingtalkThreadPool getThreadPool() {
        return this.threadPool;
    }

    public void setThreadPool(DingtalkThreadPool threadPool) {
        this.threadPool = threadPool;
    }

    public static class DingtalkHttpClient {
        @DurationUnit(value=ChronoUnit.SECONDS)
        private Duration connectTimeout = Duration.ofSeconds(30L);
        @DurationUnit(value=ChronoUnit.SECONDS)
        private Duration readTimeout = Duration.ofSeconds(30L);

        public Duration getConnectTimeout() {
            return this.connectTimeout;
        }

        public void setConnectTimeout(Duration connectTimeout) {
            this.connectTimeout = connectTimeout;
        }

        public Duration getReadTimeout() {
            return this.readTimeout;
        }

        public void setReadTimeout(Duration readTimeout) {
            this.readTimeout = readTimeout;
        }
    }

    public static class DingtalkThreadPool {
        private static final int DEFAULT_CORE_SIZE = Runtime.getRuntime().availableProcessors() + 1;
        private int coreSize = DEFAULT_CORE_SIZE;
        private int maxSize = DEFAULT_CORE_SIZE * 2;
        private int keepAliveSeconds = 60;
        private int queueCapacity = 10;
        private String threadNamePrefix = "kmc-dinger-threadpool-";

        public int getCoreSize() {
            return this.coreSize;
        }

        public void setCoreSize(int coreSize) {
            this.coreSize = coreSize;
        }

        public int getMaxSize() {
            return this.maxSize;
        }

        public void setMaxSize(int maxSize) {
            this.maxSize = maxSize;
        }

        public int getKeepAliveSeconds() {
            return this.keepAliveSeconds;
        }

        public void setKeepAliveSeconds(int keepAliveSeconds) {
            this.keepAliveSeconds = keepAliveSeconds;
        }

        public int getQueueCapacity() {
            return this.queueCapacity;
        }

        public void setQueueCapacity(int queueCapacity) {
            this.queueCapacity = queueCapacity;
        }

        public String getThreadNamePrefix() {
            return this.threadNamePrefix;
        }

        public void setThreadNamePrefix(String threadNamePrefix) {
            this.threadNamePrefix = threadNamePrefix;
        }
    }

    public static class Dinger {
        private String robotUrl;
        private String tokenId;
        private String secret;
        private boolean decrypt = false;
        private String decryptKey;
        private boolean async = false;

        public String getRobotUrl() {
            return this.robotUrl;
        }

        public void setRobotUrl(String robotUrl) {
            this.robotUrl = robotUrl;
        }

        public String getTokenId() {
            return this.tokenId;
        }

        public void setTokenId(String tokenId) {
            this.tokenId = tokenId;
        }

        public String getSecret() {
            return this.secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public boolean isDecrypt() {
            return this.decrypt;
        }

        public void setDecrypt(boolean decrypt) {
            this.decrypt = decrypt;
        }

        public String getDecryptKey() {
            return this.decryptKey;
        }

        public void setDecryptKey(String decryptKey) {
            this.decryptKey = decryptKey;
        }

        public boolean isAsync() {
            return this.async;
        }

        public void setAsync(boolean async) {
            this.async = async;
        }
    }
}

