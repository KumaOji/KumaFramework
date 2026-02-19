/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.kuma.boot.web.httpexchange;

import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class Checker {
    private static final Logger log = LoggerFactory.getLogger(Checker.class);

    private Checker() {
    }

    public static void checkUnusedConfig(HttpExchangeProperties properties) {
        Set<Class<?>> classes = Cache.getClients().keySet();
        List<HttpExchangeProperties.Channel> channels = properties.getChannels();
        for (int i = 0; i < channels.size(); ++i) {
            HttpExchangeProperties.Channel channel = channels.get(i);
            Checker.checkClassesConfiguration(classes, i, channel);
            Checker.checkClientsConfiguration(classes, i, channel);
        }
    }

    private static void checkClassesConfiguration(Set<Class<?>> classes, int i, HttpExchangeProperties.Channel channel) {
        int s = channel.getClasses().size();
        for (int j = 0; j < s; ++j) {
            Class<?> clazz = channel.getClasses().get(j);
            if (!classes.stream().noneMatch(clazz::isAssignableFrom)) continue;
            log.warn("The configuration '{}.channels[{}].clients[{}]={}' is ineffective and should be removed", new Object[]{"http-exchange", i, j, clazz.getCanonicalName()});
        }
    }

    private static void checkClientsConfiguration(Set<Class<?>> classes, int i, HttpExchangeProperties.Channel channel) {
        int size = channel.getClients().size();
        for (int j = 0; j < size; ++j) {
            String name = channel.getClients().get(j);
            if (Util.nameMatch(name, classes)) continue;
            log.warn("The configuration '{}.channels[{}].clients[{}]={}' is ineffective and should be removed", new Object[]{"http-exchange", i, j, name});
        }
    }
}

