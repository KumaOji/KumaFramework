/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.context.properties.EnableConfigurationProperties
 *  org.springframework.boot.webmvc.autoconfigure.WebMvcRegistrations
 *  org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
 */
package com.kuma.boot.web.support.version;

import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.autoconfigure.WebMvcRegistrations;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@AutoConfiguration
@EnableConfigurationProperties(value={ApiVersionProperties.class})
public class WebMvcRegistrationsConfig
implements WebMvcRegistrations {
    private final ApiVersionProperties apiVersionProperties;

    public WebMvcRegistrationsConfig(ApiVersionProperties apiVersionProperties) {
        this.apiVersionProperties = apiVersionProperties;
    }

    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        if (!this.apiVersionProperties.isEnabled()) {
            return super.getRequestMappingHandlerMapping();
        }
        LogUtils.info((String)"\u3010\u521d\u59cb\u5316\u914d\u7f6e-ApiVersionRequestMappingHandlerMapping\u3011\u9ed8\u8ba4\u914d\u7f6e\u4e3atrue\uff0c\u5f53\u524d\u73af\u5883\u4e3atrue\uff1aRESTful API\u63a5\u53e3\u7248\u672c\u63a7\u5236\uff0c\u6267\u884c\u521d\u59cb\u5316 ...", (Object[])new Object[0]);
        return new ApiVersionRequestMappingHandlerMapping(this.apiVersionProperties);
    }
}

