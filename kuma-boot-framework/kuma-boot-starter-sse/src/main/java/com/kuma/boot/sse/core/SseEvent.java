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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * SSE 事件封装，对应 W3C Server-Sent Events 规范中的单条事件字段.
 *
 * <pre>
 * SseEvent.of("hello world")                  // 仅 data
 * SseEvent.builder().event("message").data("hi").build()
 * SseEvent.heartbeat()                         // 心跳注释
 * SseEvent.done()                              // AI 流式结束标识
 * </pre>
 *
 * @author kuma
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class SseEvent {

    /** 事件 ID（对应 {@code id:} 字段）. */
    private final String id;

    /** 事件类型（对应 {@code event:} 字段），缺省为 {@code message}. */
    private final String event;

    /** 事件数据（对应 {@code data:} 字段）. */
    private final Object data;

    /** 注释（对应 {@code :} 字段），浏览器不展示但保持连接活跃. */
    private final String comment;

    /** 断线重连间隔毫秒数（对应 {@code retry:} 字段）. */
    private final Long retry;

    // ── 静态工厂 ─────────────────────────────────────────────────────────────

    /** 仅包含 data 的最简事件. */
    public static SseEvent of(Object data) {
        return SseEvent.builder().data(data).build();
    }

    /** 指定 event 类型和 data. */
    public static SseEvent of(String event, Object data) {
        return SseEvent.builder().event(event).data(data).build();
    }

    /** 心跳注释事件，用于保持长连接. */
    public static SseEvent heartbeat() {
        return SseEvent.builder().comment("heartbeat").build();
    }

    /** AI 流式响应结束标记，约定 event=done、data=[DONE]. */
    public static SseEvent done() {
        return SseEvent.builder().event("done").data("[DONE]").build();
    }

    /**
     * 将本事件转换为 {@link SseEmitter.SseEventBuilder}，供 SseEmitter.send() 使用.
     */
    public SseEmitter.SseEventBuilder toSseEventBuilder() {
        SseEmitter.SseEventBuilder builder = SseEmitter.event();
        if (id != null)      builder.id(id);
        if (event != null)   builder.name(event);
        if (data != null)    builder.data(data);
        if (comment != null) builder.comment(comment);
        if (retry != null)   builder.reconnectTime(retry);
        return builder;
    }
}
