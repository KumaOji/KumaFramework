/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.model;

import com.kuma.boot.common.enums.EventEnum;
import com.kuma.boot.common.model.Callable;
import com.kuma.boot.common.model.Pubsub;
import com.kuma.boot.common.utils.common.PropertyUtils;
import com.kuma.boot.common.utils.convert.ConvertUtils;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class PropertyCache {
    private final Pubsub<HashMap<String, Object>> pubsub;
    private final ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap();
    private boolean isStart = false;

    public PropertyCache(Pubsub<HashMap<String, Object>> pubsub) {
        this.pubsub = pubsub;
    }

    public <T> T get(String key, T defaultValue) {
        if (!this.isStart) {
            String v = PropertyUtils.getProperty(key);
            if (v == null) {
                return defaultValue;
            }
            return (T)ConvertUtils.convert((Object)v, defaultValue.getClass());
        }
        Object value = this.cache.get(key);
        if (value == null) {
            String v = PropertyUtils.getProperty(key);
            if (v != null) {
                this.cache.put(key, v);
            } else {
                this.cache.put(key, PropertyUtils.NULL);
            }
        }
        if (PropertyUtils.NULL.equals(value = this.cache.get(key))) {
            return defaultValue;
        }
        return (T)ConvertUtils.convert(value, defaultValue.getClass());
    }

    public void tryUpdateCache(final String key, final Object value) {
        if (!this.isStart) {
            return;
        }
        if (this.cache.containsKey(key)) {
            if (value == null) {
                this.cache.put(key, PropertyUtils.NULL);
            } else {
                this.cache.put(key, value);
            }
        }
        this.pubsub.pub(EventEnum.PropertyCacheUpdateEvent.toString(), new HashMap<String, Object>(this, 1){
            final /* synthetic */ PropertyCache this$0;
            {
                PropertyCache propertyCache = this$0;
                Objects.requireNonNull(propertyCache);
                this.this$0 = propertyCache;
                super(initialCapacity);
                this.put(key, value);
            }
        });
    }

    public void listenUpdateCache(String name, Callable.Action1<HashMap<String, Object>> action) {
        this.pubsub.sub(EventEnum.PropertyCacheUpdateEvent, new Pubsub.Sub<HashMap<String, Object>>(name, action));
    }

    public void clear() {
        this.cache.clear();
    }

    public void setStart(boolean start) {
        this.isStart = start;
    }
}

