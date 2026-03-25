package com.kuma.boot.oss.common.configuration;

import com.kuma.boot.oss.common.propeties.OssProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@AutoConfiguration
@ConditionalOnProperty(
   prefix = "kuma.boot.oss",
   name = {"enabled"},
   havingValue = "true"
)
@EnableConfigurationProperties({OssProperties.class})
public class OssAutoConfiguration {
}
