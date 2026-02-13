/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.model;

import com.kuma.boot.common.enums.EventEnum;
import com.kuma.boot.common.model.Callable;
import com.kuma.boot.common.utils.log.LogUtils;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Pubsub<T> {
    private final Map<String, ConcurrentHashMap<String, Sub<T>>> subscribeList = new ConcurrentHashMap<String, ConcurrentHashMap<String, Sub<T>>>();
    private final Object lock = new Object();

    public void pub(String event, T data) {
        ConcurrentHashMap<String, Sub<T>> subs = this.subscribeList.get(event);
        if (subs != null) {
            for (Map.Entry<String, Sub<T>> sub : subs.entrySet()) {
                try {
                    sub.getValue().action.invoke(data);
                }
                catch (Exception e) {
                    LogUtils.error(e, "\u5206\u53d1\u8ba2\u9605\u5931\u8d25", new Object[0]);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void sub(String event, Sub<T> action) {
        if (!this.subscribeList.containsKey(event)) {
            Object object = this.lock;
            synchronized (object) {
                if (!this.subscribeList.containsKey(event)) {
                    this.subscribeList.putIfAbsent(event, new ConcurrentHashMap(1));
                }
            }
        }
        this.subscribeList.get(event).putIfAbsent(action.name, action);
    }

    public void sub(EventEnum event, Sub<T> action) {
        this.sub(event.toString(), action);
    }

    public boolean removeSub(String event, String subName) {
        ConcurrentHashMap<String, Sub<T>> subs = this.subscribeList.get(event);
        if (subs != null) {
            subs.remove(subName);
            if (subs.isEmpty()) {
                this.subscribeList.remove(event);
            }
            return true;
        }
        return false;
    }

    public Map<String, ConcurrentHashMap<String, Sub<T>>> getSubscribeList() {
        return this.subscribeList;
    }

    public Object getLock() {
        return this.lock;
    }

    public static class Sub<T> {
        private String name;
        private Callable.Action1<T> action;

        public Sub(String name, Callable.Action1<T> action) {
            this.name = name;
            this.action = action;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Callable.Action1<T> getAction() {
            return this.action;
        }

        public void setAction(Callable.Action1<T> action) {
            this.action = action;
        }
    }
}

