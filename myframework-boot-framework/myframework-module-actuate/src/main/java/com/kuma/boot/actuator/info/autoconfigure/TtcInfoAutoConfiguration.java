/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.boot.actuate.autoconfigure.info.ConditionalOnEnabledInfoContributor
 *  org.springframework.boot.actuate.autoconfigure.info.InfoContributorFallback
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.context.annotation.Bean
 *  org.springframework.core.annotation.Order
 */
package com.kuma.boot.actuator.info.autoconfigure;

import com.kuma.boot.actuator.info.KmcInfoContributor;
import com.kuma.boot.actuator.info.KmcObservation;
import org.springframework.boot.actuate.autoconfigure.info.ConditionalOnEnabledInfoContributor;
import org.springframework.boot.actuate.autoconfigure.info.InfoContributorFallback;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

@AutoConfiguration
@ConditionalOnEnabledInfoContributor(value="kmc", fallback=InfoContributorFallback.DISABLE)
public class KmcInfoAutoConfiguration {
    @Bean
    @Order(value=-2147483638)
    public KmcInfoContributor kmcInfoContributor() {
        return new KmcInfoContributor();
    }

    @Bean
    @Order(value=-2147483638)
    public KmcObservation kmcObservation() {
        return this.kmcObservation();
    }
}

