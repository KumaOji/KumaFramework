//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.cache.jetcache.autoconfigure;

import com.alicp.jetcache.CacheManager;
import com.kuma.boot.cache.jetcache.autoconfigure.properties.JetCacheProperties;
import com.kuma.boot.cache.jetcache.enhance.HerodotusCacheManager;
import com.kuma.boot.cache.jetcache.enhance.JetCacheCreateCacheFactory;
import com.kuma.boot.cache.jetcache.utils.JetCacheUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@AutoConfiguration(
        after = {com.alicp.jetcache.autoconfigure.JetCacheAutoConfiguration.class}
)
@EnableConfigurationProperties({JetCacheProperties.class})
@ConditionalOnProperty(
        prefix = "kuma.boot.cache.jetcache",
        name = {"enabled"},
        havingValue = "true",
        matchIfMissing = true
)
public class JetCacheAutoConfiguration implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(JetCacheAutoConfiguration.class, "kuma-boot-starter-cache-jetcache", new String[0]);
    }

    @Bean
    @ConditionalOnClass({CacheManager.class})
    public JetCacheCreateCacheFactory jetCacheCreateCacheFactory(CacheManager jcCacheManager) {
        JetCacheCreateCacheFactory factory = new JetCacheCreateCacheFactory(jcCacheManager);
        JetCacheUtils.setJetCacheCreateCacheFactory(factory);
        LogUtils.trace("[kmc] |- Bean [Jet Cache Create Cache Factory] Auto Configure.", new Object[0]);
        return factory;
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean
    public HerodotusCacheManager herodotusCacheManager(JetCacheCreateCacheFactory jetCacheCreateCacheFactory, JetCacheProperties cacheProperties) {
        HerodotusCacheManager herodotusCacheManager = new HerodotusCacheManager(jetCacheCreateCacheFactory, cacheProperties);
        herodotusCacheManager.setAllowNullValues(cacheProperties.getAllowNullValues());
        LogUtils.trace("[kmc] |- Bean [Jet Cache Herodotus Cache Manager] Auto Configure.", new Object[0]);
        return herodotusCacheManager;
    }
}
