/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.context.annotation.Import
 */
package com.kuma.boot.web.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.web.validation.aop.VerifyParametersAspect;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@Import(value={VerifyParametersAspect.class})
@AutoConfiguration
public class ValidationAopAutoConfiguration
implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(ValidationAopAutoConfiguration.class, (String)"kuma-boot-starter-web", (String[])new String[0]);
    }
}

