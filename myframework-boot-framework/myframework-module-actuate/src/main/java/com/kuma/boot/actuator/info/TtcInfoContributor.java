/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.support.version.KmcVersion
 *  org.springframework.boot.actuate.info.Info$Builder
 *  org.springframework.boot.actuate.info.InfoContributor
 */
package com.kuma.boot.actuator.info;

import com.kuma.boot.common.support.version.KmcVersion;
import java.util.Collections;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;

public class KmcInfoContributor
implements InfoContributor {
    public void contribute(Info.Builder builder) {
        builder.withDetail("kmc-version", Collections.singletonMap("version", KmcVersion.getVersion()));
    }
}

