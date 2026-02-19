/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.date.DateUtil
 *  cn.hutool.core.date.StopWatch
 *  cn.hutool.core.exceptions.ExceptionUtil
 *  com.kuma.boot.common.utils.log.LogUtils
 *  com.kuma.boot.common.utils.servlet.RequestUtils
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.web.context.request.NativeWebRequest
 */
package com.kuma.boot.web.exception.handler;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.servlet.RequestUtils;
import com.kuma.boot.web.exception.domain.ExceptionMessage;
import com.kuma.boot.web.exception.domain.ExceptionNoticeResponse;
import com.kuma.boot.web.exception.properties.ExceptionHandleProperties;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.request.NativeWebRequest;

public abstract class AbstractExceptionHandler
extends Thread
implements ExceptionHandler,
InitializingBean,
DisposableBean {
    private final BlockingQueue<QueueMessage> queue = new LinkedBlockingQueue<QueueMessage>();
    private static final String NULL_MESSAGE_KEY = "";
    protected final ExceptionHandleProperties config;
    private volatile boolean flag = true;
    private final Map<String, ExceptionMessage> messages;
    private String mac;
    private String hostname;
    private String ip;
    private final String applicationName;

    protected AbstractExceptionHandler(ExceptionHandleProperties config, String applicationName) {
        this.config = config;
        this.messages = new ConcurrentHashMap<String, ExceptionMessage>(config.getMax() * 2);
        this.applicationName = applicationName;
        try {
            InetAddress ia = InetAddress.getLocalHost();
            this.hostname = ia.getHostName();
            this.ip = ia.getHostAddress();
            byte[] macByte = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < macByte.length; ++i) {
                sb.append(String.format("%02X%s", macByte[i], i < macByte.length - 1 ? "-" : NULL_MESSAGE_KEY));
            }
            this.mac = sb.toString();
        }
        catch (Exception e) {
            this.mac = "\u83b7\u53d6\u5931\u8d25!";
        }
    }

    @Override
    public void run() {
        StopWatch interval = new StopWatch();
        long threadId = Thread.currentThread().threadId();
        while (this.flag) {
            int i = 0;
            while (i < this.config.getMax() && interval.getTotalTimeSeconds() < (double)this.config.getTime()) {
                ExceptionMessage message;
                QueueMessage queueMessage = null;
                try {
                    queueMessage = this.queue.poll(i == 0 ? TimeUnit.HOURS.toSeconds(1L) : 10L, TimeUnit.SECONDS);
                }
                catch (InterruptedException e) {
                    this.interrupt();
                }
                if (queueMessage == null) continue;
                String key = queueMessage.getTraceId();
                if (i++ == 0) {
                    interval.stop();
                    message = this.toMessage(queueMessage);
                    message.setThreadId(threadId);
                    this.messages.put(key, message);
                    continue;
                }
                if (this.messages.containsKey(key)) {
                    this.messages.put(key, this.messages.get(key).increment());
                    continue;
                }
                message = this.toMessage(queueMessage);
                message.setThreadId(threadId);
                this.messages.put(key, message);
            }
            if (this.messages.size() > 0) {
                this.messages.forEach((k, v) -> {
                    try {
                        ExceptionNoticeResponse response = this.send((ExceptionMessage)v);
                        if (!response.isSuccess()) {
                            LogUtils.error((String)"\u6d88\u606f\u901a\u77e5\u53d1\u9001\u5931\u8d25! msg: {}", (Object[])new Object[]{response.getErrMsg()});
                        }
                    }
                    catch (Exception e) {
                        LogUtils.error((String)"\u6d88\u606f\u901a\u77e5\u65f6\u53d1\u751f\u5f02\u5e38", (Object[])new Object[]{e});
                    }
                });
                this.messages.clear();
            }
            interval.stop();
        }
    }

    public ExceptionMessage toMessage(QueueMessage queueMessage) {
        ExceptionMessage message = new ExceptionMessage();
        message.setTraceId(queueMessage.getTraceId());
        message.setNumber(1);
        message.setMac(this.mac);
        message.setApplicationName(this.applicationName);
        message.setHostname(this.hostname);
        message.setIp(this.ip);
        message.setRequestUri(queueMessage.getRequestUri());
        message.setTime(DateUtil.formatTime((Date)new Date()));
        message.setStack(ExceptionUtil.stacktraceToString((Throwable)queueMessage.getThrowable(), (int)this.config.getLength()).replace("\\r", NULL_MESSAGE_KEY));
        return message;
    }

    public abstract ExceptionNoticeResponse send(ExceptionMessage var1);

    @Override
    public void handle(NativeWebRequest req, Throwable throwable, String traceId) {
        try {
            String requestUri = RequestUtils.getRequest() == null ? "uri not found" : RequestUtils.getRequest().getRequestURI();
            boolean ignore = false;
            if (Boolean.FALSE.equals(this.config.getIgnoreChild())) {
                ignore = this.config.getIgnoreExceptions().contains(throwable.getClass());
            } else {
                for (Class<? extends Throwable> ignoreException : this.config.getIgnoreExceptions()) {
                    if (!ignoreException.isAssignableFrom(throwable.getClass())) continue;
                    ignore = true;
                    break;
                }
            }
            QueueMessage message = new QueueMessage(throwable, traceId, requestUri);
            if (!ignore) {
                this.queue.put(message);
            }
        }
        catch (InterruptedException e) {
            this.interrupt();
        }
        catch (Exception e) {
            LogUtils.error((String)"\u5f80\u5f02\u5e38\u6d88\u606f\u961f\u5217\u63d2\u5165\u65b0\u5f02\u5e38\u65f6\u51fa\u9519", (Object[])new Object[]{e});
        }
    }

    public void afterPropertiesSet() {
        this.initThread();
    }

    protected void initThread() {
        this.setName("exception-notice");
        this.start();
    }

    public void destroy() throws Exception {
        this.flag = false;
        this.interrupt();
    }

    private static class QueueMessage {
        private Throwable throwable;
        private String traceId;
        private String requestUri;

        public QueueMessage(Throwable throwable, String traceId, String requestUri) {
            this.throwable = throwable;
            this.traceId = traceId;
            this.requestUri = requestUri;
        }

        public Throwable getThrowable() {
            return this.throwable;
        }

        public void setThrowable(Throwable throwable) {
            this.throwable = throwable;
        }

        public String getTraceId() {
            return this.traceId;
        }

        public void setTraceId(String traceId) {
            this.traceId = traceId;
        }

        public String getRequestUri() {
            return this.requestUri;
        }

        public void setRequestUri(String requestUri) {
            this.requestUri = requestUri;
        }
    }
}

