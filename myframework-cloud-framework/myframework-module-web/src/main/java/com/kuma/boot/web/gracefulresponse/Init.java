/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.annotation.PostConstruct
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.kuma.boot.web.gracefulresponse;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Init {
    private final Logger logger = LoggerFactory.getLogger(Init.class);

    @PostConstruct
    public void init() {
        this.logger.info("Graceful Response:\u5df2\u542f\u52a8");
    }
}

