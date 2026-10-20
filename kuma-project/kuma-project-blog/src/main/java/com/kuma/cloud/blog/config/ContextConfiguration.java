package com.kuma.cloud.blog.config;

import com.kuma.boot.core.autoconfigure.CoreAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import com.kuma.boot.core.utils.context.EnableContextUtils;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/** Explicitly loads the Kuma starter features used by this application. */
@Configuration(proxyBeanMethods = false)
@EnableContextUtils
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ImportAutoConfiguration({
        CoreAutoConfiguration.class
})
public class ContextConfiguration {
}
