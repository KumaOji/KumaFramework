package com.kuma.boot.web.validation.spel.core.parse;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * ApplicationContext工具类，便于在一些非Spring管理的类中使用ApplicationContext的功能
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/4/29
 */
public class SpelValidatorBeanRegistrar implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) {
        SpelValidatorBeanRegistrar.applicationContext = applicationContext;
    }

}
