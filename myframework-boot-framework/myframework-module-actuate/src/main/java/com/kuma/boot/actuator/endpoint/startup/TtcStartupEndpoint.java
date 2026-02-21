/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.core.startup.StartupReporter
 *  com.kuma.boot.core.startup.StartupReporter$StartupStaticsModel
 *  org.springframework.boot.actuate.endpoint.annotation.Endpoint
 *  org.springframework.boot.actuate.endpoint.annotation.ReadOperation
 *  org.springframework.boot.actuate.endpoint.annotation.WriteOperation
 */
package com.kuma.boot.actuator.endpoint.startup;

import com.kuma.boot.core.startup.StartupReporter;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;

@Endpoint(id="kmcstartup")
public class KmcStartupEndpoint {
    private final StartupReporter startupReporter;

    public KmcStartupEndpoint(StartupReporter startupReporter) {
        this.startupReporter = startupReporter;
    }

    @ReadOperation
    public StartupReporter.StartupStaticsModel startupSnapshot() {
        return this.startupReporter.getStartupStaticsModel();
    }

    @WriteOperation
    public StartupReporter.StartupStaticsModel startup() {
        return this.startupReporter.drainStartupStaticsModel();
    }
}

