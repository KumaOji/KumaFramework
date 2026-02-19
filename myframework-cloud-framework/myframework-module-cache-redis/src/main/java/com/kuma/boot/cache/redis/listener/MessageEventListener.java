/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.data.redis.connection.MessageListener
 *  org.springframework.data.redis.listener.Topic
 */
package com.kuma.boot.cache.redis.listener;

import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.Topic;

public interface MessageEventListener
extends MessageListener {
    public Topic topic();
}

