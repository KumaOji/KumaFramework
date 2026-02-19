/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.kuma.boot.web.laytpl.js;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsConsole {
    private static Logger log = LoggerFactory.getLogger(JsConsole.class);

    public void debug() {
        log.debug("debug by console.");
    }

    public void debug(String message) {
        log.debug(message);
    }

    public void debug(String message, Object ... args) {
        log.debug(message, args);
    }

    public void log() {
        log.info("log by console.");
    }

    public void log(String message) {
        log.info(message);
    }

    public void log(String message, Object ... args) {
        log.info(message, args);
    }

    public void info() {
        log.info("info by console.");
    }

    public void info(String message) {
        log.info(message);
    }

    public void info(String message, Object ... args) {
        log.info(message, args);
    }

    public void warn() {
        log.warn("warn by console.");
    }

    public void warn(String message) {
        log.warn(message);
    }

    public void warn(String message, Object ... args) {
        log.warn(message, args);
    }

    public void trace() {
        log.trace("trace by console.");
    }

    public void trace(String message) {
        log.trace(message);
    }

    public void trace(String message, Object ... args) {
        log.trace(message, args);
    }

    public void error() {
        log.error("error by console.");
    }

    public void error(String message) {
        log.error(message);
    }

    public void error(String message, Object ... args) {
        log.error(message, args);
    }
}

