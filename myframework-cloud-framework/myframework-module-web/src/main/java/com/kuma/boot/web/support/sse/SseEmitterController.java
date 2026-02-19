/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.web.bind.annotation.GetMapping
 *  org.springframework.web.bind.annotation.PathVariable
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RestController
 *  org.springframework.web.servlet.mvc.method.annotation.SseEmitter
 */
package com.kuma.boot.web.support.sse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping(value={"/sse"})
public class SseEmitterController {
    @Autowired
    private SseEmitterService sseEmitterService;

    @GetMapping(path={"/subscribe/{id}"}, produces={"text/event-stream"})
    public SseEmitter subscribe(@PathVariable(value="id") String id) {
        return this.sseEmitterService.connect(id);
    }

    @GetMapping(value={"/close/{id}"})
    public void close(@PathVariable(value="id") String id) {
        SseEmitterUTF8 sseEmitterUTF8 = this.sseEmitterService.getSseEmitter(id);
        if (sseEmitterUTF8 != null) {
            sseEmitterUTF8.complete();
        }
    }
}

