package com.kuma.boot.web.autoconfigure.customizer;

import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;

public interface KmcWebMvcConfigurer {

    default void configurePathMatch(PathMatchConfigurer configurer) {
    }

}
