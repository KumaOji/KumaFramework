//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.support.graphic.configuration;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.captcha.support.core.ResourceProviderAutoConfiguration;
import com.kuma.boot.captcha.support.core.provider.ResourceProvider;
import com.kuma.boot.captcha.support.graphic.renderer.ArithmeticCaptchaRenderer;
import com.kuma.boot.captcha.support.graphic.renderer.ChineseCaptchaRenderer;
import com.kuma.boot.captcha.support.graphic.renderer.ChineseGifCaptchaRenderer;
import com.kuma.boot.captcha.support.graphic.renderer.SpecCaptchaRenderer;
import com.kuma.boot.captcha.support.graphic.renderer.SpecGifCaptchaRenderer;
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
public class GraphicCaptchaConfiguration {
    private static final Logger log = LoggerFactory.getLogger(GraphicCaptchaConfiguration.class);
    @Autowired
    private RedisRepository redisRepository;

    @PostConstruct
    public void postConstruct() {
        log.debug("SDK [Engine Captcha Graphic] Auto Configure.");
    }

    @Bean({"ARITHMETIC"})
    @ConditionalOnBean({ResourceProvider.class})
    public ArithmeticCaptchaRenderer arithmeticCaptchaRenderer(ResourceProvider resourceProvider) {
        ArithmeticCaptchaRenderer arithmeticCaptchaRenderer = new ArithmeticCaptchaRenderer(this.redisRepository, "cache:token:captcha:graphic:");
        arithmeticCaptchaRenderer.setResourceProvider(resourceProvider);
        log.trace("Bean [Arithmetic Captcha Renderer] Auto Configure.");
        return arithmeticCaptchaRenderer;
    }

    @Bean({"CHINESE"})
    @ConditionalOnBean({ResourceProvider.class})
    public ChineseCaptchaRenderer chineseCaptchaRenderer(ResourceProvider resourceProvider) {
        ChineseCaptchaRenderer chineseCaptchaRenderer = new ChineseCaptchaRenderer(this.redisRepository, "cache:token:captcha:graphic:");
        chineseCaptchaRenderer.setResourceProvider(resourceProvider);
        log.trace("Bean [Chinese Captcha Renderer] Auto Configure.");
        return chineseCaptchaRenderer;
    }

    @Bean({"CHINESE_GIF"})
    @ConditionalOnBean({ResourceProvider.class})
    public ChineseGifCaptchaRenderer chineseGifCaptchaRenderer(ResourceProvider resourceProvider) {
        ChineseGifCaptchaRenderer chineseGifCaptchaRenderer = new ChineseGifCaptchaRenderer(this.redisRepository, "cache:token:captcha:graphic:");
        chineseGifCaptchaRenderer.setResourceProvider(resourceProvider);
        log.trace(" Bean [Chinese Gif Captcha Renderer] Auto Configure.");
        return chineseGifCaptchaRenderer;
    }

    @Bean({"SPEC_GIF"})
    @ConditionalOnBean({ResourceProvider.class})
    public SpecGifCaptchaRenderer specGifCaptchaRenderer(ResourceProvider resourceProvider) {
        SpecGifCaptchaRenderer specGifCaptchaRenderer = new SpecGifCaptchaRenderer(this.redisRepository, "cache:token:captcha:graphic:");
        specGifCaptchaRenderer.setResourceProvider(resourceProvider);
        log.trace(" Bean [Spec Gif Captcha Renderer] Auto Configure.");
        return specGifCaptchaRenderer;
    }

    @Bean({"SPEC"})
    @ConditionalOnBean({ResourceProvider.class})
    public SpecCaptchaRenderer specCaptchaRenderer(ResourceProvider resourceProvider) {
        SpecCaptchaRenderer specCaptchaRenderer = new SpecCaptchaRenderer(this.redisRepository, "cache:token:captcha:graphic:");
        specCaptchaRenderer.setResourceProvider(resourceProvider);
        log.trace("Bean [Spec Captcha Renderer] Auto Configure.");
        return specCaptchaRenderer;
    }
}
