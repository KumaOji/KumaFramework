/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.support.version.KmcVersion
 *  org.springframework.boot.health.contributor.AbstractHealthIndicator
 *  org.springframework.boot.health.contributor.Health$Builder
 */
package com.kuma.boot.actuator.health;

import com.kuma.boot.common.support.version.KmcVersion;
import org.springframework.boot.health.contributor.AbstractHealthIndicator;
import org.springframework.boot.health.contributor.Health;

public class KmcHealthIndicator
extends AbstractHealthIndicator {
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        builder.withDetail("kmc-version", (Object)KmcVersion.getVersion());
        builder.up();
    }
}

