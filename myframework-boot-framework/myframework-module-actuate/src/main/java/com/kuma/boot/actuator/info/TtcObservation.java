/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  io.micrometer.observation.Observation
 *  io.micrometer.observation.ObservationRegistry
 */
package com.kuma.boot.actuator.info;

import com.kuma.boot.common.utils.log.LogUtils;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

public class KmcObservation {
    private final ObservationRegistry observationRegistry;

    public KmcObservation(ObservationRegistry observationRegistry) {
        this.observationRegistry = observationRegistry;
    }

    public void doSomething() {
        Observation.createNotStarted((String)"doSomething", (ObservationRegistry)this.observationRegistry).lowCardinalityKeyValue("locale", "en-US").highCardinalityKeyValue("userId", "42").observe(() -> LogUtils.info((String)"ObservationRegistry doSomething==========================\u6267\u884c", (Object[])new Object[0]));
    }
}

