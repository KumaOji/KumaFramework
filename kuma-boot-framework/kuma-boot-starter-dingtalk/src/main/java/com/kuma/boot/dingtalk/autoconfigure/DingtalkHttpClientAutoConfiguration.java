/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.context.annotation.Bean
 *  org.springframework.http.client.ClientHttpRequestFactory
 *  org.springframework.http.client.SimpleClientHttpRequestFactory
 *  org.springframework.web.client.RestTemplate
 */
package com.kuma.boot.dingtalk.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.dingtalk.autoconfigure.properties.DingtalkProperties;
import com.kuma.boot.dingtalk.support.DingerHttpClient;
import com.kuma.boot.dingtalk.support.DingerHttpTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@AutoConfiguration
public class DingtalkHttpClientAutoConfiguration
implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(DingtalkHttpClientAutoConfiguration.class, (String)"kuma-boot-starter-dingtalk", (String[])new String[0]);
    }

    @Bean(name={"dingerClientHttpRequestFactory"})
    public ClientHttpRequestFactory dingerClientHttpRequestFactory(DingtalkProperties dingtalkProperties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout((int)dingtalkProperties.getHttpclient().getReadTimeout().toMillis());
        factory.setConnectTimeout((int)dingtalkProperties.getHttpclient().getConnectTimeout().toMillis());
        return factory;
    }

    @Bean(name={"dingerRestTemplate"})
    public RestTemplate dingerRestTemplate(ClientHttpRequestFactory dingerClientHttpRequestFactory) {
        return new RestTemplate(dingerClientHttpRequestFactory);
    }

    @Bean
    public DingerHttpClient dingerHttpClient(@Autowired @Qualifier(value="dingerRestTemplate") RestTemplate restTemplate) {
        return new DingerHttpTemplate(restTemplate);
    }
}

