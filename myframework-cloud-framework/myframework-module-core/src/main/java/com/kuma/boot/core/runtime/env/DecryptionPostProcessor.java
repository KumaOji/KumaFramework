package com.kuma.boot.core.runtime.env;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

//org.springframework.boot.EnvironmentPostProcessor=\
// com.kuma.boot.core.runtime.env.DecryptionPostProcessor

/**
 * DecryptionPostProcessor
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class DecryptionPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment( ConfigurableEnvironment environment, SpringApplication application ) {
        MutablePropertySources propertySources = environment.getPropertySources();
        for (PropertySource<?> source : propertySources) {
            if (source instanceof MapPropertySource) {
                MapPropertySource mapPropertySource = (MapPropertySource) source;
                // 这里使用replace方法对source进行替换.
                propertySources.replace(mapPropertySource.getName(),
                        new DecryptPropertySource(mapPropertySource.getName(), mapPropertySource));
            }
        }

    }
}

