package com.kuma.boot.core.runtime.env;

//import com.kuma.boot.core.utils.CoreUtils;

import com.kuma.boot.core.utils.CoreUtils;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;

//org.springframework.boot.EnvironmentPostProcessor=\
// com.kuma.boot.core.runtime.env.KmcEnvironmentPostProcessor

/**
 * KmcEnvironmentPostProcessor
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class KmcEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment( ConfigurableEnvironment environment, SpringApplication application ) {
        MutablePropertySources propertySources = environment.getPropertySources();

        propertySources.addLast(CoreUtils.getVersionSource(application.getClass(), environment));
        propertySources.addLast(CoreUtils.getUrlSource());

    }
}

