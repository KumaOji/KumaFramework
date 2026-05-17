package com.kuma.boot.core.utils;

import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.common.startup.SofaBootConstants;
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
     * 获取 Spring 应用名称（即 {@code spring.application.name}）。
     *
     * <p><b>注意：</b>{@link ApplicationContext#getApplicationName()} 在 Spring Boot 中始终返回 {@code ""}，
     * 不能用于获取 {@code spring.application.name}，请始终使用本方法代替。</p>
     *
     * @param context application context，可为 null
     * @return 应用名称，未配置时返回空字符串
     */
    public static String getApplicationName(ApplicationContext context) {
        if (context == null) {
            return "";
        }
        String name = context.getEnvironment().getProperty(SofaBootConstants.APP_NAME_KEY);
        return name != null ? name : "";
    }

    /**
     * 获取当前 Spring 应用名称（从 Spring 容器中取 ApplicationContext）。
     *
     * @return 应用名称，未配置或容器未就绪时返回空字符串
     */
    public static String getApplicationName() {
        return getApplicationName(getApplicationContext());
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
