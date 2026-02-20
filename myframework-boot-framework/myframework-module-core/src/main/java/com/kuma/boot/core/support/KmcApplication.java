package com.kuma.boot.core.support;

import com.kuma.boot.core.startup.StartupSpringApplication;

/**
 * KmcApplication
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class KmcApplication {

    public static StartupSpringApplication startApplication( Class<?>... primarySources ) {
        return new StartupSpringApplication(primarySources)
                .setKmcBanner();
    }
}
