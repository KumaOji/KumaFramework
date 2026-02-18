package com.kuma.boot.core.utils;

import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.common.utils.servlet.RequestUtils;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.server.context.ConfigurableWebServerApplicationContext;
import org.springframework.context.ApplicationContext;

/**
 * RequestWebUtils
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class RequestWebUtils extends RequestUtils {

    /**
     * getConfigurableWebServerApplicationContext
     *
     * @return {@link ConfigurableWebServerApplicationContext }
     * @since 2023-01-03 11:33:19
     */
    public static ConfigurableWebServerApplicationContext getConfigurableWebServerApplicationContext() {
        ApplicationContext context = ContextUtils.getApplicationContext();
        if (context instanceof ConfigurableWebServerApplicationContext) {
            return (ConfigurableWebServerApplicationContext) context;
        }
        return null;
    }

    /**
     * getBaseUrl
     *
     * @return {@link String }
     * @since 2023-01-03 11:33:19
     */
    public static String getBaseUrl() {
        if (!isWeb()) {
            return "";
        }
        WebServer webServer = getConfigurableWebServerApplicationContext().getWebServer();
        if (webServer == null) {
            return "";
        }
        return "http://" + getIpAddress() + ":" + webServer.getPort();
    }


    /**
     * isWeb
     *
     * @return boolean
     * @since 2023-01-03 11:33:19
     */
    public static boolean isWeb() {
        return getConfigurableWebServerApplicationContext() != null;
    }
}
