/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
 *  org.springframework.boot.context.properties.EnableConfigurationProperties
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Import
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 *  org.springframework.web.client.RestTemplate
 */
package com.kuma.boot.dingtalk.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.dingtalk.autoconfigure.properties.DingtalkProperties;
import com.kuma.boot.dingtalk.exception.ConfigurationException;
import com.kuma.boot.dingtalk.model.DingerConfigurerAdapter;
import com.kuma.boot.dingtalk.model.DingerManagerBuilder;
import com.kuma.boot.dingtalk.model.DingerRobot;
import com.kuma.boot.dingtalk.model.DingerSender;
import com.kuma.boot.dingtalk.session.DingerSessionFactory;
import com.kuma.boot.dingtalk.session.SessionConfiguration;
import com.kuma.boot.dingtalk.spring.DingerSessionFactoryBean;
import com.kuma.boot.dingtalk.support.CustomMessage;
import com.kuma.boot.dingtalk.support.DingTalkSignAlgorithm;
import com.kuma.boot.dingtalk.support.DingerAsyncCallback;
import com.kuma.boot.dingtalk.support.DingerExceptionCallback;
import com.kuma.boot.dingtalk.support.DingerHttpClient;
import com.kuma.boot.dingtalk.support.DingerIdGenerator;
import java.util.concurrent.Executor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@AutoConfiguration(after={DingtalkBeanAutoConfiguration.class, DingtalkHttpClientAutoConfiguration.class, DingtalkThreadPoolAutoConfiguration.class})
@Import(value={DingtalkBeanAutoConfiguration.class, DingtalkHttpClientAutoConfiguration.class, DingtalkThreadPoolAutoConfiguration.class})
@EnableConfigurationProperties(value={DingtalkProperties.class})
@ConditionalOnProperty(prefix="kuma.boot.dingtalk", value={"enabled"}, havingValue="true")
public class DingtalkAutoConfiguration
implements InitializingBean {
    private final DingtalkProperties properties;
    private final ResourceLoader resourceLoader;

    public DingtalkAutoConfiguration(DingtalkProperties dingtalkProperties, ResourceLoader resourceLoader) {
        this.properties = dingtalkProperties;
        this.resourceLoader = resourceLoader;
    }

    public void afterPropertiesSet() throws Exception {
        LogUtils.started(DingtalkAutoConfiguration.class, (String)"kuma-boot-starter-dingtalk", (String[])new String[0]);
        this.checkConfigFileExists();
    }

    @Bean
    @ConditionalOnMissingBean(value={DingerConfigurerAdapter.class})
    public DingerConfigurerAdapter dingerConfigurerAdapter() {
        return new DingerConfigurerAdapter();
    }

    @Bean
    public DingerManagerBuilder dingerManagerBuilder(@Autowired @Qualifier(value="dingerRestTemplate") RestTemplate restTemplate, DingerExceptionCallback dingerExceptionCallback, @Autowired @Qualifier(value="textMessage") CustomMessage textMessage, @Autowired @Qualifier(value="markDownMessage") CustomMessage markDownMessage, DingTalkSignAlgorithm dingerSignAlgorithm, DingerIdGenerator dingerIdGenerator, @Autowired @Qualifier(value="dingtalkExecutor") Executor dingTalkExecutor, DingerAsyncCallback dingerAsyncCallback, DingerHttpClient dingerHttpClient) {
        return new DingerManagerBuilder(restTemplate, dingerExceptionCallback, textMessage, markDownMessage, dingerSignAlgorithm, dingerIdGenerator, dingTalkExecutor, dingerAsyncCallback, dingerHttpClient);
    }

    @Bean
    public DingerSender dingerSender(DingerConfigurerAdapter dingerConfigurerAdapter, DingerManagerBuilder dingerManagerBuilder) {
        try {
            dingerConfigurerAdapter.configure(dingerManagerBuilder);
        }
        catch (Exception ex) {
            throw new ConfigurationException(ex);
        }
        return new DingerRobot(this.properties, dingerManagerBuilder);
    }

    @Bean
    @ConditionalOnMissingBean
    public DingerSessionFactory dingerSessionFactory(DingerRobot dingerRobot) throws Exception {
        DingerSessionFactoryBean factory = new DingerSessionFactoryBean();
        factory.setConfiguration(SessionConfiguration.of(this.properties, dingerRobot));
        return factory.getObject();
    }

    private void checkConfigFileExists() {
        if (StringUtils.hasText((String)this.properties.getDingerLocations())) {
            Resource resource = this.resourceLoader.getResource(this.properties.getDingerLocations());
            Assert.state((boolean)resource.exists(), (String)("Cannot find config location: " + String.valueOf(resource) + " (please add config file or check your Dinger configuration)"));
        }
    }
}

