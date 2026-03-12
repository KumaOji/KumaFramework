//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.support.hutool.configuration;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.captcha.support.core.ResourceProviderAutoConfiguration;
import com.kuma.boot.captcha.support.core.provider.ResourceProvider;
import com.kuma.boot.captcha.support.hutool.renderer.CircleCaptchaRenderer;
import com.kuma.boot.captcha.support.hutool.renderer.GifCaptchaRenderer;
import com.kuma.boot.captcha.support.hutool.renderer.LineCaptchaRenderer;
import com.kuma.boot.captcha.support.hutool.renderer.ShearCaptchaRenderer;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(
        after = {ResourceProviderAutoConfiguration.class}
)
@ConditionalOnProperty(
        prefix = "kuma.boot.captcha.tmp",
        name = {"enabled"},
        havingValue = "true"
)
public class HutoolCaptchaConfiguration {
    private static final Logger log = LoggerFactory.getLogger(HutoolCaptchaConfiguration.class);
    @Autowired
    private RedisRepository redisRepository;

    @PostConstruct
    public void postConstruct() {
        log.debug("SDK [Engine Captcha Hutool] Auto Configure.");
    }

    @Bean({"HUTOOL_LINE"})
    @ConditionalOnBean({ResourceProvider.class})
    public LineCaptchaRenderer lineCaptchaRenderer(ResourceProvider resourceProvider) {
        LineCaptchaRenderer lineCaptchaRenderer = new LineCaptchaRenderer(this.redisRepository, "cache:token:captcha:graphic:");
        lineCaptchaRenderer.setResourceProvider(resourceProvider);
        log.trace("Bean [Hutool Line Captcha Renderer] Auto Configure.");
        return lineCaptchaRenderer;
    }

    @Bean({"HUTOOL_CIRCLE"})
    @ConditionalOnBean({ResourceProvider.class})
    public CircleCaptchaRenderer circleCaptchaRenderer(ResourceProvider resourceProvider) {
        CircleCaptchaRenderer circleCaptchaRenderer = new CircleCaptchaRenderer(this.redisRepository, "cache:token:captcha:graphic:");
        circleCaptchaRenderer.setResourceProvider(resourceProvider);
        log.trace(" Bean [Hutool Circle Captcha Renderer] Auto Configure.");
        return circleCaptchaRenderer;
    }

    @Bean({"HUTOOL_SHEAR"})
    @ConditionalOnBean({ResourceProvider.class})
    public ShearCaptchaRenderer shearCaptchaRenderer(ResourceProvider resourceProvider) {
        ShearCaptchaRenderer shearCaptchaRenderer = new ShearCaptchaRenderer(this.redisRepository, "cache:token:captcha:graphic:");
        shearCaptchaRenderer.setResourceProvider(resourceProvider);
        log.trace(" Bean [Hutool Shear Captcha Renderer] Auto Configure.");
        return shearCaptchaRenderer;
    }

    @Bean({"HUTOOL_GIF"})
    @ConditionalOnBean({ResourceProvider.class})
    public GifCaptchaRenderer gifCaptchaRenderer(ResourceProvider resourceProvider) {
        GifCaptchaRenderer gifCaptchaRenderer = new GifCaptchaRenderer(this.redisRepository, "cache:token:captcha:graphic:");
        gifCaptchaRenderer.setResourceProvider(resourceProvider);
        log.trace("Bean [Hutool Gif Captcha Renderer] Auto Configure.");
        return gifCaptchaRenderer;
    }
}
