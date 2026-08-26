package com.kuma.cloud.lab.snowflake.config;

import com.kuma.boot.common.utils.common.SequenceUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SnowflakeLabConfiguration {

    @Bean
    SequenceUtils snowflakeSequenceUtils(SnowflakeLabProperties properties) {
        return new SequenceUtils(properties.getWorkerId(), properties.getDatacenterId());
    }

}
