/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.boot.actuate.endpoint.annotation.ReadOperation
 *  org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint
 *  org.springframework.boot.health.contributor.Health
 */
package com.kuma.boot.actuator.endpoint.kmc;

import com.kuma.boot.actuator.health.KmcHealthIndicator;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;
import org.springframework.boot.health.contributor.Health;

@WebEndpoint(id="kmchealth")
public class KmcHealthEndPoint {
    private final KmcHealthIndicator kmcHealthIndicator;

    public KmcHealthEndPoint(KmcHealthIndicator kmcHealthIndicator) {
        this.kmcHealthIndicator = kmcHealthIndicator;
    }

    @ReadOperation
    public Health health() {
        return this.kmcHealthIndicator.health(true);
    }
}

