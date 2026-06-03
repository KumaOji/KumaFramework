/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.sse.core;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.sse.autoconfigure.properties.SseProperties;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 基于内存 {@link ConcurrentHashMap} 的默认 SSE 连接管理器.
 *
 * <p>线程安全：connect / send / close 均可在多线程环境下并发调用。
 *
 * @author kuma
 */
public class DefaultSseEmitterManager implements SseEmitterManager {

    private final SseProperties properties;

    /** clientId → SseEmitter 映射，值永不为 null. */
    private final ConcurrentHashMap<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public DefaultSseEmitterManager(SseProperties properties) {
        this.properties = properties;
    }

    @Override
    public SseEmitter connect(String clientId) {
        long timeoutMs = properties.getTimeout().toMillis();
        return connect(clientId, timeoutMs);
    }

    @Override
    public SseEmitter connect(String clientId, long timeoutMs) {
        // 若旧连接存在，先完成它再替换，避免客户端 zombie 连接
        SseEmitter old = emitters.remove(clientId);
        if (old != null) {
            try { old.complete(); } catch (Exception ignored) {}
        }

        SseEmitter emitter = new SseEmitter(timeoutMs);

        emitter.onCompletion(() -> remove(clientId));
        emitter.onTimeout(()    -> remove(clientId));
        emitter.onError(e       -> remove(clientId));

        emitters.put(clientId, emitter);
        LogUtils.info("[kuma-sse] client connected: {}", clientId);
        return emitter;
    }

    @Override
    public void send(String clientId, SseEvent event) {
        SseEmitter emitter = emitters.get(clientId);
        if (emitter == null) {
            return;
        }
        doSend(clientId, emitter, event);
    }

    @Override
    public void sendToAll(SseEvent event) {
        emitters.forEach((clientId, emitter) -> doSend(clientId, emitter, event));
    }

    @Override
    public void close(String clientId) {
        SseEmitter emitter = emitters.remove(clientId);
        if (emitter != null) {
            try { emitter.complete(); } catch (Exception ignored) {}
            LogUtils.info("[kuma-sse] client closed: {}", clientId);
        }
    }

    @Override
    public void closeAll() {
        emitters.forEach((clientId, emitter) -> {
            try { emitter.complete(); } catch (Exception ignored) {}
        });
        emitters.clear();
        LogUtils.info("[kuma-sse] all connections closed");
    }

    @Override
    public Set<String> getClientIds() {
        return Collections.unmodifiableSet(emitters.keySet());
    }

    @Override
    public int getClientCount() {
        return emitters.size();
    }

    @Override
    public boolean hasClient(String clientId) {
        return emitters.containsKey(clientId);
    }

    // ── 内部方法 ──────────────────────────────────────────────────────────────

    private void doSend(String clientId, SseEmitter emitter, SseEvent event) {
        try {
            emitter.send(event.toSseEventBuilder());
        } catch (IOException e) {
            // 发送失败说明连接已断开，移除并完成
            remove(clientId);
        } catch (Exception e) {
            LogUtils.error(e, "[kuma-sse] send error for client: {}", clientId);
            remove(clientId);
        }
    }

    private void remove(String clientId) {
        emitters.remove(clientId);
        LogUtils.info("[kuma-sse] client disconnected: {}", clientId);
    }
}
