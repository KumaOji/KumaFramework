/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.http.HttpUtil
 *  com.kuma.boot.common.utils.log.LogUtils
 *  com.kuma.boot.core.runtime.listener.ApplicationStartedListener
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnBean
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication$Type
 *  org.springframework.boot.context.event.ApplicationStartedEvent
 *  org.springframework.boot.web.server.autoconfigure.ServerProperties
 *  org.springframework.boot.web.server.servlet.context.ServletWebServerApplicationContext
 *  org.springframework.context.annotation.Import
 */
package com.kuma.boot.web.autoconfigure;

import cn.hutool.http.HttpUtil;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.core.runtime.listener.ApplicationStartedListener;
import com.kuma.boot.web.controller.RequestController;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.web.server.autoconfigure.ServerProperties;
import org.springframework.boot.web.server.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import(value={RequestController.class})
@ConditionalOnWebApplication(type=ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnBean(value={ServletWebServerApplicationContext.class})
public class PostOpenApiAutoConfiguration
extends ApplicationStartedListener
implements InitializingBean {
    @Autowired
    private ServletWebServerApplicationContext servletWebServerApplicationContext;
    @Autowired
    private ServerProperties serverProperties;

    public void afterPropertiesSet() throws Exception {
        LogUtils.started(PostOpenApiAutoConfiguration.class, (String)"kuma-boot-starter-web", (String[])new String[0]);
    }

    protected void eventCallback(ApplicationStartedEvent event) {
        int port = this.servletWebServerApplicationContext.getWebServer().getPort();
        new Thread(new PostOpenApiRunnable("127.0.0.1", port)).start();
    }

    public static class PostOpenApiRunnable
    implements Runnable {
        private final String ip;
        private final int port;

        public PostOpenApiRunnable(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }

        @Override
        public void run() {
            LogUtils.info((String)"\u5f00\u59cb\u8fdb\u884cHealthcheck", (Object[])new Object[0]);
            try {
                Thread.sleep(10000L);
            }
            catch (InterruptedException e) {
                LogUtils.error((Throwable)e);
                return;
            }
            try {
                HttpUtil.get((String)("http://" + this.ip + ":" + this.port + "/v3/api-docs"), (int)5000);
            }
            catch (Exception ignored) {
                LogUtils.info((String)"\u8fdb\u884cHealthcheck\u5931\u8d25", (Object[])new Object[0]);
            }
            LogUtils.info((String)"\u8fdb\u884cHealthcheck\u6210\u529f", (Object[])new Object[0]);
        }
    }
}

