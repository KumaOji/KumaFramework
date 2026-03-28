package com.kuma.boot.frp.autoconfigure;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.frp.autoconfigure.properties.FrpProperties;
import com.kuma.boot.frp.manager.FrpClientManager;
import org.springframework.aot.hint.annotation.ImportRuntimeHints;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * FRP 客户端自动配置
 *
 * <p>通过 {@code kuma.boot.frp.enabled=true} 激活
 *
 * @author kuma
 */
@AutoConfiguration
@ImportRuntimeHints(FrpRuntimeHintsRegistrar.class)
@EnableConfigurationProperties(FrpProperties.class)
@ConditionalOnProperty(prefix = FrpProperties.PREFIX, name = "enabled", havingValue = "true")
public class FrpAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        LogUtils.started(FrpAutoConfiguration.class, StarterNameConstants.FRP_STARTER);
    }

    @Bean
    public FrpClientManager frpClientManager(FrpProperties properties) {
        return new FrpClientManager(properties);
    }
}
