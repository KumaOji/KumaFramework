package com.kuma.boot.core.utils;

import com.kuma.boot.common.utils.context.ContextUtils;
import org.springframework.boot.web.server.context.ConfigurableWebServerApplicationContext;
import org.springframework.context.ApplicationContext;

/**
 * BootContextUtils
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class BootContextUtils extends ContextUtils {

    /**
     * isWeb
     *
     * @return boolean
     * @since 2021-09-02 17:37:36
     */
    public static boolean isWeb() {
        return getConfigurableWebServerApplicationContext() != null;
    }

    /**
     * getConfigurableWebServerApplicationContext
     *
     * @return {@link ConfigurableWebServerApplicationContext }
     * @since 2021-09-02 17:37:38
     */
    public static ConfigurableWebServerApplicationContext getConfigurableWebServerApplicationContext() {
        ApplicationContext context = getApplicationContext();
        if (context instanceof ConfigurableWebServerApplicationContext) {
            return (ConfigurableWebServerApplicationContext) context;
        }
        return null;
    }
}
