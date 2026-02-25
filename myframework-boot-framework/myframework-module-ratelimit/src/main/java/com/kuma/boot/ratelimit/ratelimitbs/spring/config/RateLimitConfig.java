/*
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.config.BeanFactoryPostProcessor
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 *  org.springframework.context.annotation.ImportAware
 *  org.springframework.core.annotation.AnnotationAttributes
 *  org.springframework.core.type.AnnotationMetadata
 */
package com.kuma.boot.ratelimit.ratelimitbs.spring.config;

import com.kuma.boot.ratelimit.ratelimitbs.api.core.IRateLimit;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitConfigService;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitMethodService;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitRejectListener;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitTokenService;
import com.kuma.boot.ratelimit.ratelimitbs.core.bs.RateLimitBs;
import com.kuma.boot.ratelimit.ratelimitbs.core.core.RateLimits;
import com.kuma.boot.ratelimit.ratelimitbs.core.support.config.RateLimitConfigService;
import com.kuma.boot.ratelimit.ratelimitbs.core.support.method.RateLimitMethodService;
import com.kuma.boot.ratelimit.ratelimitbs.core.support.reject.RateLimitRejectListenerException;
import com.kuma.boot.ratelimit.ratelimitbs.core.support.token.RateLimitTokenService;
import com.kuma.boot.ratelimit.ratelimitbs.extend.cache.CommonCacheServiceMap;
import com.kuma.boot.ratelimit.ratelimitbs.extend.cache.ICommonCacheService;
import com.kuma.boot.ratelimit.ratelimitbs.extend.timer.ITimer;
import com.kuma.boot.ratelimit.ratelimitbs.extend.timer.Timers;
import com.kuma.boot.ratelimit.ratelimitbs.spring.annotation.EnableRateLimit;
import com.kuma.boot.ratelimit.ratelimitbs.spring.aop.RateLimitAspect;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

@Configuration
@Import(value={RateLimitAspect.class})
public class RateLimitConfig
implements ImportAware,
BeanFactoryPostProcessor {
    private AnnotationAttributes enableAttributes;
    private ConfigurableListableBeanFactory beanFactory;

    @Bean(value={"rateLimitBs"})
    public RateLimitBs rateLimitBs() {
        IRateLimit rateLimit = (IRateLimit)this.beanFactory.getBean(this.enableAttributes.getString("value"), IRateLimit.class);
        ITimer timer = (ITimer)this.beanFactory.getBean(this.enableAttributes.getString("timer"), ITimer.class);
        ICommonCacheService cacheService = (ICommonCacheService)this.beanFactory.getBean(this.enableAttributes.getString("cacheService"), ICommonCacheService.class);
        IRateLimitConfigService configService = (IRateLimitConfigService)this.beanFactory.getBean(this.enableAttributes.getString("configService"), IRateLimitConfigService.class);
        IRateLimitTokenService tokenService = (IRateLimitTokenService)this.beanFactory.getBean(this.enableAttributes.getString("tokenService"), IRateLimitTokenService.class);
        IRateLimitMethodService methodService = (IRateLimitMethodService)this.beanFactory.getBean(this.enableAttributes.getString("methodService"), IRateLimitMethodService.class);
        IRateLimitRejectListener rejectListener = (IRateLimitRejectListener)this.beanFactory.getBean(this.enableAttributes.getString("rejectListener"), IRateLimitRejectListener.class);
        String cacheKeyNamespace = this.enableAttributes.getString("cacheKeyNamespace");
        return RateLimitBs.newInstance().rateLimit(rateLimit).timer(timer).cacheService(cacheService).configService(configService).tokenService(tokenService).methodService(methodService).rejectListener(rejectListener).cacheKeyNamespace(cacheKeyNamespace);
    }

    @Bean(value={"rateLimit"})
    public IRateLimit rateLimit() {
        return RateLimits.tokenBucket();
    }

    @Bean(value={"rateLimitTimer"})
    public ITimer rateLimitTimer() {
        return Timers.system();
    }

    @Bean(value={"rateLimitCacheService"})
    public ICommonCacheService rateLimitCacheService() {
        return new CommonCacheServiceMap();
    }

    @Bean(value={"rateLimitConfigService"})
    public IRateLimitConfigService rateLimitConfigService() {
        return new RateLimitConfigService();
    }

    @Bean(value={"rateLimitTokenService"})
    public IRateLimitTokenService rateLimitTokenService() {
        return new RateLimitTokenService();
    }

    @Bean(value={"rateLimitMethodService"})
    public IRateLimitMethodService rateLimitMethodService() {
        return new RateLimitMethodService();
    }

    @Bean(value={"rateLimitRejectListener"})
    public IRateLimitRejectListener rateLimitRejectListener() {
        return new RateLimitRejectListenerException();
    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public void setImportMetadata(AnnotationMetadata annotationMetadata) {
        this.enableAttributes = AnnotationAttributes.fromMap((Map)annotationMetadata.getAnnotationAttributes(EnableRateLimit.class.getName(), false));
        if (this.enableAttributes == null) {
            throw new IllegalArgumentException("@EnableRateLimit is not present on importing class " + annotationMetadata.getClassName());
        }
    }
}

