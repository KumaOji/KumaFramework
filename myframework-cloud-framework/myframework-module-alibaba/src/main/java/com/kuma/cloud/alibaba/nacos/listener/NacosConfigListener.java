/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.cloud.nacos.NacosConfigManager
 *  com.alibaba.nacos.api.config.ConfigType
 *  com.alibaba.nacos.api.config.annotation.NacosConfigListener
 *  com.alibaba.nacos.api.config.listener.Listener
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
 *  org.springframework.context.annotation.Configuration
 */
package com.kuma.cloud.alibaba.nacos.listener;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.listener.Listener;
import com.kuma.boot.common.utils.log.LogUtils;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Executor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

public class NacosConfigListener
implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(NacosConfigListener.class, (String)"kuma-boot-starter-core", (String[])new String[0]);
    }

    @com.alibaba.nacos.api.config.annotation.NacosConfigListener(dataId="kuma-cloud", type=ConfigType.YAML)
    public void onReceived(Properties value) {
        LogUtils.info((String)"kuma cloud on received from nacos properties data : {}", (Object[])new Object[]{value});
    }

    @Configuration
    @ConditionalOnProperty(prefix="spring.cloud.nacos.config", value={"enabled"}, havingValue="true", matchIfMissing=true)
    public static class KmcNacosConfigListener
    implements InitializingBean {
        private final NacosConfigManager nacosConfigManager;

        public KmcNacosConfigListener(NacosConfigManager nacosConfigManager) {
            this.nacosConfigManager = nacosConfigManager;
        }

        public void afterPropertiesSet() throws Exception {
            this.nacosConfigManager.getConfigService().addListener("test", "DEFAULT_GROUP", new Listener(this){
                {
                    Objects.requireNonNull(this$0);
                }

                public Executor getExecutor() {
                    return null;
                }

                public void receiveConfigInfo(String configInfo) {
                    LogUtils.info((String)"kuma cloud on received from nacos config info : {}", (Object[])new Object[]{configInfo});
                }
            });
        }
    }
}

