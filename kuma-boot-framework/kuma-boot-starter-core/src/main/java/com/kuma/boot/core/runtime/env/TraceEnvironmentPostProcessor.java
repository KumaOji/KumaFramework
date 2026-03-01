/*
 * Copyright 2021 spring-boot-extension the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.kuma.boot.core.runtime.env;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.util.ClassUtils.getDefaultClassLoader;

/**
 * <p>
 * 用于设置trace日志格式
 * </p>
 */
public class TraceEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String PROPERTY_SOURCE_NAME = "traceProperties";

    private static final String PROBABILITY_KEY = "management.tracing.sampling.probability";

    private static final String LEVEL_KEY = "logging.pattern.level";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        //todo 可以打开 但是会污染logging.pattern.level

        //if (application.getWebApplicationType() == WebApplicationType.SERVLET) {
        //	if (isPresent() && environment.getProperty("management.tracing.enabled", Boolean.class, true)) {
        //		Map<String, Object> map = new HashMap<>();
        //		if (!environment.containsProperty(PROBABILITY_KEY)) {
        //			map.put(PROBABILITY_KEY, 1.0);
        //		}
        //		if (!environment.containsProperty(LEVEL_KEY)) {
        //			if (StringUtils.hasText(environment.getProperty("spring.application.name"))) {
        //				map.put(LEVEL_KEY, "%5p [${spring.application.name:},%X{traceId:-},%X{spanId:-}]");
        //			} else {
        //				map.put(LEVEL_KEY, "%5p [%X{traceId:-},%X{spanId:-}]");
        //			}
        //		}
        //		MutablePropertySources propertySources = environment.getPropertySources();
        //		if (!propertySources.contains(PROPERTY_SOURCE_NAME) && !map.isEmpty()) {
        //			MapPropertySource target = new MapPropertySource(PROPERTY_SOURCE_NAME, map);
        //			propertySources.addLast(target);
        //		}
        //	}
        //}
    }

    private boolean isPresent() {
        ClassLoader defaultClassLoader = getDefaultClassLoader();
        return ClassUtils
                .isPresent("org.springframework.boot.actuator.autoconfigure.tracing.OpenTelemetryAutoConfiguration",defaultClassLoader)
                && ClassUtils.isPresent("io.micrometer.tracing.Tracer",defaultClassLoader)
                && ClassUtils.isPresent("io.micrometer.tracing.otel.bridge.OtelTracer",defaultClassLoader);
    }

}
