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

import java.util.Set;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * SSE 连接管理器，负责 emitter 的生命周期和事件投递.
 *
 * <p>典型用法：
 * <pre>
 * // Controller 中建立连接
 * {@literal @}GetMapping(value = "/sse/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
 * public SseEmitter connect(@RequestParam String clientId) {
 *     return sseEmitterManager.connect(clientId);
 * }
 *
 * // 业务层推送消息
 * sseEmitterManager.send(clientId, SseEvent.of("message", payload));
 * </pre>
 *
 * @author kuma
 */
public interface SseEmitterManager {

    /**
     * 创建并注册一个 SseEmitter，超时使用配置默认值.
     *
     * @param clientId 客户端唯一标识
     * @return 新建的 {@link SseEmitter}
     */
    SseEmitter connect(String clientId);

    /**
     * 创建并注册一个 SseEmitter，指定超时（毫秒，0 为永不超时）.
     *
     * @param clientId  客户端唯一标识
     * @param timeoutMs 超时毫秒数
     * @return 新建的 {@link SseEmitter}
     */
    SseEmitter connect(String clientId, long timeoutMs);

    /**
     * 向指定客户端推送事件；客户端不存在时静默忽略.
     *
     * @param clientId 客户端唯一标识
     * @param event    SSE 事件
     */
    void send(String clientId, SseEvent event);

    /**
     * 向所有已连接客户端广播事件.
     *
     * @param event SSE 事件
     */
    void sendToAll(SseEvent event);

    /**
     * 主动关闭指定客户端连接.
     *
     * @param clientId 客户端唯一标识
     */
    void close(String clientId);

    /** 关闭所有连接. */
    void closeAll();

    /**
     * 返回当前已连接的客户端 ID 集合（只读快照）.
     */
    Set<String> getClientIds();

    /** 当前活跃连接数. */
    int getClientCount();

    /**
     * 判断指定客户端是否存在活跃连接.
     *
     * @param clientId 客户端唯一标识
     */
    boolean hasClient(String clientId);
}
