/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.attr;

import com.kuma.boot.common.support.attr.Attribute;
import com.kuma.boot.common.utils.common.ArgUtils;
import com.kuma.boot.common.utils.lang.ObjectUtils;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AttributeContext
implements Attribute {
    private final Map<String, Object> context;

    public AttributeContext() {
        this.context = new ConcurrentHashMap<String, Object>();
    }

    public AttributeContext(Map<String, Object> map) {
        this.context = new ConcurrentHashMap<String, Object>(map);
    }

    protected AttributeContext putAttrMap(Map<String, ?> map) {
        ArgUtils.notNull(map, "map");
        this.context.putAll(map);
        return this;
    }

    protected Set<Map.Entry<String, Object>> entrySet() {
        return this.context.entrySet();
    }

    @Override
    public AttributeContext putAttr(String key, Object value) {
        this.context.put(key, value);
        return this;
    }

    @Override
    public Object getAttr(String key) {
        return this.context.get(key);
    }

    @Override
    public Optional<Object> getAttrOptional(String key) {
        Object object = this.getAttr(key);
        return Optional.ofNullable(object);
    }

    @Override
    public String getAttrString(String key) {
        Object object = this.getAttr(key);
        return ObjectUtils.objectToString(object);
    }

    @Override
    public Boolean getAttrBoolean(String key) {
        Optional<Object> objectOptional = this.getAttrOptional(key);
        return (Boolean)objectOptional.orElse(false);
    }

    @Override
    public Character getAttrCharacter(String key) {
        Optional<Object> objectOptional = this.getAttrOptional(key);
        assert (objectOptional.orElse("") instanceof Character);
        return (Character)objectOptional.orElse("");
    }

    @Override
    public Byte getAttrByte(String key) {
        Optional<Object> objectOptional = this.getAttrOptional(key);
        assert (objectOptional.orElse(0) instanceof Byte);
        return (Byte)objectOptional.orElse(0);
    }

    @Override
    public Short getAttrShort(String key) {
        Optional<Object> objectOptional = this.getAttrOptional(key);
        assert (objectOptional.orElse(0) instanceof Short);
        return (Short)objectOptional.orElse(0);
    }

    @Override
    public Integer getAttrInteger(String key) {
        Optional<Object> objectOptional = this.getAttrOptional(key);
        return (Integer)objectOptional.orElse(0);
    }

    @Override
    public Float getAttrFloat(String key) {
        Optional<Object> objectOptional = this.getAttrOptional(key);
        assert (objectOptional.orElse(0) instanceof Float);
        return (Float)objectOptional.orElse(0);
    }

    @Override
    public Double getAttrDouble(String key) {
        Optional<Object> objectOptional = this.getAttrOptional(key);
        assert (objectOptional.orElse(0) instanceof Double);
        return (Double)objectOptional.orElse(0);
    }

    @Override
    public Long getAttrLong(String key) {
        Optional<Object> objectOptional = this.getAttrOptional(key);
        return (Long)objectOptional.orElse(0L);
    }

    @Override
    public Attribute removeAttr(String key) {
        this.context.remove(key);
        return this;
    }

    @Override
    public boolean containsKey(String key) {
        return this.context.containsKey(key);
    }

    @Override
    public Set<String> keySet() {
        return this.context.keySet();
    }
}

