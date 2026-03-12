//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.support.behavior.configuration;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.captcha.support.behavior.renderer.JigsawCaptchaRenderer;
import com.kuma.boot.captcha.support.behavior.renderer.WordClickCaptchaRenderer;
import com.kuma.boot.captcha.support.core.ResourceProviderAutoConfiguration;
import com.kuma.boot.captcha.support.core.provider.ResourceProvider;
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
public class BehaviorCaptchaConfiguration {
    private static final Logger log = LoggerFactory.getLogger(BehaviorCaptchaConfiguration.class);
    @Autowired
    private RedisRepository redisRepository;

    @PostConstruct
    public void postConstruct() {
        log.debug("[kmc] |- SDK [Captcha Behavior] Auto Configure.");
    }

    @Bean({"JIGSAW"})
    @ConditionalOnBean({ResourceProvider.class})
    public JigsawCaptchaRenderer jigsawCaptchaRenderer(ResourceProvider resourceProvider) {
        JigsawCaptchaRenderer jigsawCaptchaRenderer = new JigsawCaptchaRenderer(this.redisRepository, "cache:token:captcha:jigsaw:");
        jigsawCaptchaRenderer.setResourceProvider(resourceProvider);
        log.trace("[kmc] |- Bean [Jigsaw Captcha Renderer] Auto Configure.");
        return jigsawCaptchaRenderer;
    }

    @Bean({"WORD_CLICK"})
    @ConditionalOnBean({ResourceProvider.class})
    public WordClickCaptchaRenderer wordClickCaptchaRenderer(ResourceProvider resourceProvider) {
        WordClickCaptchaRenderer wordClickCaptchaRenderer = new WordClickCaptchaRenderer(this.redisRepository, "cache:token:captcha:word_click:");
        wordClickCaptchaRenderer.setResourceProvider(resourceProvider);
        log.trace("[kmc] |- Bean [Word Click Captcha Renderer] Auto Configure.");
        return wordClickCaptchaRenderer;
    }
}
