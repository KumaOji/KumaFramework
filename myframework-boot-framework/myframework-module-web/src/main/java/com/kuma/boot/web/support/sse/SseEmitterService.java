/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.extra.spring.SpringUtil
 *  cn.hutool.json.JSONUtil
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.context.ApplicationEvent
 *  org.springframework.web.servlet.mvc.method.annotation.SseEmitter
 *  org.springframework.web.servlet.mvc.method.annotation.SseEmitter$SseEventBuilder
 */
package com.kuma.boot.web.support.sse;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.kuma.boot.common.utils.log.LogUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import org.springframework.context.ApplicationEvent;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class SseEmitterService {
    private Map<String, SseEmitterUTF8> sseEmitterMap = new ConcurrentHashMap<String, SseEmitterUTF8>();

    public SseEmitter connect(String id) {
        SseEmitterUTF8 sseEmitter = new SseEmitterUTF8(60000L);
        try {
            sseEmitter.send(SseEmitter.event().comment("\u521b\u5efa\u8fde\u63a5\u6210\u529f"));
        }
        catch (Exception e) {
            LogUtils.error((String)"\u521b\u5efa\u8fde\u63a5\u5931\u8d25 , {} ", (Object[])new Object[]{e.getMessage()});
        }
        sseEmitter.onCompletion(this.completionCallBack(id));
        sseEmitter.onTimeout(this.timeoutCallBack(id));
        sseEmitter.onError(this.errorCallBack(id));
        this.sseEmitterMap.put(id, sseEmitter);
        LogUtils.info((String)"\u5f53\u524d\u7528\u6237\u603b\u8fde\u63a5\u6570 : {} ", (Object[])new Object[]{this.sseEmitterMap.size()});
        SpringUtil.getApplicationContext().publishEvent((ApplicationEvent)new SseConnectionEvent(id));
        return sseEmitter;
    }

    public SseEmitterUTF8 getSseEmitter(String id) {
        return this.sseEmitterMap.get(id);
    }

    public Set<String> getAllSseId() {
        return this.sseEmitterMap.keySet();
    }

    public boolean send(String id, SseMessage<?> message) {
        if (this.sseEmitterMap.containsKey(id)) {
            try {
                SseEmitter.SseEventBuilder sseEventBuilder = SseEmitter.event().data((Object)JSONUtil.toJsonStr(message));
                if (!message.getTopic().equals("")) {
                    sseEventBuilder.name(message.getTopic());
                }
                this.sseEmitterMap.get(id).send(sseEventBuilder);
                return true;
            }
            catch (Exception e) {
                LogUtils.error((String)"[{}]\u63a8\u9001\u5f02\u5e38:{}", (Object[])new Object[]{id, e.getMessage()});
            }
        } else {
            LogUtils.error((String)"{}:\u4e0d\u5b58\u5728", (Object[])new Object[]{id});
        }
        return false;
    }

    public void send(SseMessage<?> message) {
        SseEmitter.SseEventBuilder sseEventBuilder = SseEmitter.event().data(message);
        if (!message.getTopic().equals("")) {
            sseEventBuilder.name(message.getTopic());
        }
        this.sseEmitterMap.forEach((k, v) -> {
            try {
                v.send(sseEventBuilder);
            }
            catch (Exception e) {
                LogUtils.error((String)"\u7528\u6237[{}]\u63a8\u9001\u5f02\u5e38:{}", (Object[])new Object[]{k, e.getMessage()});
            }
        });
    }

    public void send(Set<String> ids, SseMessage<?> message) {
        ids.forEach(id -> this.send((String)id, message));
    }

    private Runnable completionCallBack(String id) {
        return () -> {
            LogUtils.info((String)"\u7ed3\u675f\u8fde\u63a5\uff1a{}", (Object[])new Object[]{id});
            this.sseEmitterMap.remove(id);
        };
    }

    private Runnable timeoutCallBack(String id) {
        return () -> {
            LogUtils.info((String)"\u8fde\u63a5\u8d85\u65f6\uff1a{}", (Object[])new Object[]{id});
            this.sseEmitterMap.remove(id);
        };
    }

    private Consumer<Throwable> errorCallBack(String id) {
        return throwable -> {
            LogUtils.info((String)"\u8fde\u63a5\u5f02\u5e38\uff1a{}", (Object[])new Object[]{id});
            this.sseEmitterMap.remove(id);
        };
    }
}

