/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.github.xiaoymin.knife4j.core.util.StrUtil
 *  com.github.xiaoymin.knife4j.insight.config.Knife4jInsightCommonInfo
 *  com.github.xiaoymin.knife4j.insight.upload.Knife4jInsightUploadRunner
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.boot.CommandLineRunner
 *  org.springframework.context.EnvironmentAware
 *  org.springframework.core.env.Environment
 */
package com.kuma.boot.springdoc.knife4j.spring.insight;

import com.github.xiaoymin.knife4j.core.util.StrUtil;
import com.github.xiaoymin.knife4j.insight.config.Knife4jInsightCommonInfo;
import com.github.xiaoymin.knife4j.insight.upload.Knife4jInsightUploadRunner;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.springdoc.knife4j.spring.configuration.insight.Knife4jInsightProperties;
import com.kuma.boot.springdoc.knife4j.spring.util.EnvironmentUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

public class Knife4jInsightDiscoveryBootstrapper
implements CommandLineRunner,
EnvironmentAware {
    final Knife4jInsightProperties insightProperties;
    private Environment environment;

    public Knife4jInsightDiscoveryBootstrapper(Knife4jInsightProperties insightProperties) {
        this.insightProperties = insightProperties;
    }

    public void run(String ... args) throws Exception {
        String serviceName = this.insightProperties.getServiceName();
        if (StrUtil.isBlank((CharSequence)serviceName)) {
            serviceName = EnvironmentUtils.resolveString(this.environment, "spring.application.name", "");
        }
        if (StrUtil.isBlank((CharSequence)serviceName)) {
            LogUtils.warn((String)"service-name must set one,upload refused.", (Object[])new Object[0]);
            return;
        }
        Knife4jInsightCommonInfo commonInfo = new Knife4jInsightCommonInfo();
        commonInfo.setContextPath(EnvironmentUtils.resolveContextPath(this.environment));
        commonInfo.setSpec("OpenAPI3");
        commonInfo.setServiceName(serviceName);
        commonInfo.setSecret(this.insightProperties.getSecret());
        commonInfo.setNamespace(this.insightProperties.getNamespace());
        commonInfo.setPort(EnvironmentUtils.resolveString(this.environment, "server.port", "8080"));
        commonInfo.setServer(this.insightProperties.getServer());
        Knife4jInsightUploadRunner uploadRunner = new Knife4jInsightUploadRunner(commonInfo);
        new Thread((Runnable)uploadRunner).start();
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}

