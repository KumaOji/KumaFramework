/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 *  org.springframework.core.env.Environment
 */
package com.kuma.boot.web.utils;

import com.kuma.boot.common.utils.log.LogUtils;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;

public class SpringUtils
implements ApplicationContextAware,
DisposableBean {
    private static ApplicationContext applicationContext = null;
    private static final List<CallBack> CALL_BACKS = new ArrayList<CallBack>();
    private static boolean addCallback = true;

    public static synchronized void addCallBacks(CallBack callBack) {
        if (addCallback) {
            CALL_BACKS.add(callBack);
        } else {
            LogUtils.warn((String)"CallBack\uff1a{} \u5df2\u65e0\u6cd5\u6dfb\u52a0\uff01\u7acb\u5373\u6267\u884c", (Object[])new Object[]{callBack.getCallBackName()});
            callBack.executor();
        }
    }

    public static <T> T getBean(String name) {
        SpringUtils.assertContextInjected();
        return (T)applicationContext.getBean(name);
    }

    public static <T> T getBean(Class<T> requiredType) {
        SpringUtils.assertContextInjected();
        return (T)applicationContext.getBean(requiredType);
    }

    public static <T> T getProperties(String property, T defaultValue, Class<T> requiredType) {
        Object result = defaultValue;
        try {
            result = SpringUtils.getBean(Environment.class).getProperty(property, requiredType);
        }
        catch (Exception exception) {
            // empty catch block
        }
        return result;
    }

    public static String getProperties(String property) {
        return SpringUtils.getProperties(property, null, String.class);
    }

    public static <T> T getProperties(String property, Class<T> requiredType) {
        return SpringUtils.getProperties(property, null, requiredType);
    }

    private static void assertContextInjected() {
        if (applicationContext == null) {
            throw new IllegalStateException("applicationContext\u5c5e\u6027\u672a\u6ce8\u5165, \u8bf7\u5728applicationContext.xml\u4e2d\u5b9a\u4e49SpringContextHolder\u6216\u5728SpringBoot\u542f\u52a8\u7c7b\u4e2d\u6ce8\u518cSpringContextHolder.");
        }
    }

    private static void clearHolder() {
        LogUtils.debug((String)("\u6e05\u9664SpringContextHolder\u4e2d\u7684ApplicationContext:" + String.valueOf(applicationContext)), (Object[])new Object[0]);
        applicationContext = null;
    }

    public void destroy() {
        SpringUtils.clearHolder();
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringUtils.applicationContext != null) {
            LogUtils.warn((String)("SpringContextHolder\u4e2d\u7684ApplicationContext\u88ab\u8986\u76d6, \u539f\u6709ApplicationContext\u4e3a:" + String.valueOf(SpringUtils.applicationContext)), (Object[])new Object[0]);
        }
        SpringUtils.applicationContext = applicationContext;
        if (addCallback) {
            for (CallBack callBack : CALL_BACKS) {
                callBack.executor();
            }
            CALL_BACKS.clear();
        }
        addCallback = false;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static interface CallBack {
        public void executor();

        default public String getCallBackName() {
            return Thread.currentThread().threadId() + ":" + this.getClass().getName();
        }
    }
}

