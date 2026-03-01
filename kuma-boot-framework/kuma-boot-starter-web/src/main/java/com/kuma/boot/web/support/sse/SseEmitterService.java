package com.kuma.boot.web.support.sse;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * @author zhanghongbin
 */
public class SseEmitterService {

    private Map<String, SseEmitterUTF8> sseEmitterMap = new ConcurrentHashMap<>();

    public SseEmitter connect(String id) {
        // 60秒
        SseEmitterUTF8 sseEmitter = new SseEmitterUTF8(60_000L);
        try {
            sseEmitter.send(SseEmitter.event().comment("创建连接成功"));
        } catch (Exception e) {
            LogUtils.error("创建连接失败 , {} ", e.getMessage());
        }
        sseEmitter.onCompletion(completionCallBack(id));
        sseEmitter.onTimeout(timeoutCallBack(id));
        sseEmitter.onError(errorCallBack(id));
        sseEmitterMap.put(id, sseEmitter);
        LogUtils.info("当前用户总连接数 : {} ", sseEmitterMap.size());
        SpringUtil.getApplicationContext().publishEvent(new com.kuma.boot.web.support.sse.SseConnectionEvent(id));
        return sseEmitter;
    }

    public SseEmitterUTF8 getSseEmitter(String id) {
        return sseEmitterMap.get(id);
    }

    public Set<String> getAllSseId() {
        return this.sseEmitterMap.keySet();
    }

    /**
     * 指定id发送
     *
     * @param id id
     * @param message 消息对象
     * @return true,false
     */
    public boolean send(String id, SseMessage<?> message) {
        if (sseEmitterMap.containsKey(id)) {
            try {
                SseEmitter.SseEventBuilder sseEventBuilder = SseEmitter.event().data(JSONUtil.toJsonStr(message));
                if (!message.getTopic().equals("")) {
                    sseEventBuilder.name(message.getTopic());
                }
                sseEmitterMap.get(id).send(sseEventBuilder);
                return true;
            } catch (Exception e) {
                LogUtils.error("[{}]推送异常:{}", id, e.getMessage());
            }
        } else {
            LogUtils.error("{}:不存在", id);
        }
        return false;
    }

    /**
     * 群发
     *
     * @param message 消息对象
     */
    public void send( SseMessage<?> message) {
        SseEmitter.SseEventBuilder sseEventBuilder = SseEmitter.event().data(message);
        if (!message.getTopic().equals("")) {
            sseEventBuilder.name(message.getTopic());
        }
        sseEmitterMap.forEach((k, v) -> {
            try {
                v.send(sseEventBuilder);
            } catch (Exception e) {
                LogUtils.error("用户[{}]推送异常:{}", k, e.getMessage());
            }
        });
    }

    /**
     * 指定多个id
     *
     * @param ids id列表
     * @param message 消息对象
     */
    public void send(Set<String> ids, SseMessage<?> message) {
        ids.forEach(id -> send(id, message));
    }

    private Runnable completionCallBack(String id) {
        return () -> {
            LogUtils.info("结束连接：{}", id);
            sseEmitterMap.remove(id);
        };
    }

    private Runnable timeoutCallBack(String id) {
        return () -> {
            LogUtils.info("连接超时：{}", id);
            sseEmitterMap.remove(id);
        };
    }

    private Consumer<Throwable> errorCallBack(String id) {
        return throwable -> {
            LogUtils.info("连接异常：{}", id);
            sseEmitterMap.remove(id);
        };
    }
}
