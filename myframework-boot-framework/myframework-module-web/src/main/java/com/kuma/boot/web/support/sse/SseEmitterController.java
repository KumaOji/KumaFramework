package com.kuma.boot.web.support.sse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * SSE 控制器，仅当 SseEmitterService 存在时注册，避免 SpringDoc 扫描时触发 NoSuchBeanDefinitionException
 *
 * @author zhanghongbin
 */
@RestController
@RequestMapping("/sse")
@ConditionalOnBean(SseEmitterService.class)
public class SseEmitterController {

    @Autowired
    private SseEmitterService sseEmitterService;

    /**
     * 创建连接并返回 SseEmitter
     *
     * @param id id
     * @return SseEmitter
     */
    @GetMapping(
            path = "/subscribe/{id}",
            produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public SseEmitter subscribe(@PathVariable("id") String id) {
        return sseEmitterService.connect(id);
    }

    /**
     * 关闭当前连接
     * @param id id
     */
    @GetMapping(value = "/close/{id}")
    public void close(@PathVariable("id") String id) {
        SseEmitterUTF8 sseEmitterUTF8 = sseEmitterService.getSseEmitter(id);
        if (sseEmitterUTF8 != null) sseEmitterUTF8.complete();
    }
}
