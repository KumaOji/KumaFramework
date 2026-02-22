/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.enums;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public enum RejectedExecutionHandlerPolicy {
    CALLER_RUNS{

        @Override
        public RejectedExecutionHandler getRejectedHandler() {
            return new ThreadPoolExecutor.CallerRunsPolicy();
        }
    }
    ,
    ABORT{

        @Override
        public RejectedExecutionHandler getRejectedHandler() {
            return new ThreadPoolExecutor.AbortPolicy();
        }
    }
    ,
    DISCARD_OLDEST{

        @Override
        public RejectedExecutionHandler getRejectedHandler() {
            return new ThreadPoolExecutor.DiscardOldestPolicy();
        }
    }
    ,
    DISCARD{

        @Override
        public RejectedExecutionHandler getRejectedHandler() {
            return new ThreadPoolExecutor.DiscardPolicy();
        }
    };


    public abstract RejectedExecutionHandler getRejectedHandler();
}

