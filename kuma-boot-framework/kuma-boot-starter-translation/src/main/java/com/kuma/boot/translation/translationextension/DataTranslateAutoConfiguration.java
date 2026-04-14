package com.kuma.boot.translation.translationextension;

import com.kuma.boot.translation.translationextension.service.DictTranslationService;
import com.kuma.boot.translation.translationextension.service.NoOpDictTranslationService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@AutoConfiguration
public class DataTranslateAutoConfiguration {

    /**
     * {@link DictTranslationService} 需由业务对接字典数据；未提供时使用空实现，避免启动阶段缺少依赖。
     */
    @Bean
    @ConditionalOnMissingBean(DictTranslationService.class)
    public DictTranslationService dictTranslationService() {
        return new NoOpDictTranslationService();
    }
}
