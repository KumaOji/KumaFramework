package com.kuma.cloud.gateway.filter;

import com.kuma.boot.core.enums.KmcEnvEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;

import static org.assertj.core.api.Assertions.assertThat;

class GatewayProfileTest {
    @Test
    @ResourceLock("systemProperties")
    void preservesSpringProfileCaseAndAdditionalObservabilityProfile() throws Exception {
        var envString = KmcEnvEnum.class.getDeclaredField("currEnvStr");
        var envEnum = KmcEnvEnum.class.getDeclaredField("currEnv");
        envString.setAccessible(true);
        envEnum.setAccessible(true);
        Object oldString = envString.get(null);
        Object oldEnum = envEnum.get(null);
        String oldProfiles = System.getProperty("spring.profiles.active");
        try {
            System.setProperty("spring.profiles.active", "dev,observability");
            envString.set(null, null);
            envEnum.set(null, null);
            assertThat(KmcEnvEnum.getEnv()).isEqualTo("dev,observability");
            assertThat(KmcEnvEnum.getCurrEnv()).isEqualTo(KmcEnvEnum.DEV);
            assertThat(System.getProperty("spring.profiles.active")).isEqualTo("dev,observability");
        } finally {
            if (oldProfiles == null) System.clearProperty("spring.profiles.active");
            else System.setProperty("spring.profiles.active", oldProfiles);
            envString.set(null, oldString);
            envEnum.set(null, oldEnum);
        }
    }
}
