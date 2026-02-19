/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.common.support.attr;

import com.kuma.boot.common.utils.common.ArgUtils;
import com.kuma.boot.common.utils.lang.ObjectUtils;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 属性上下文上下文
 *
 * <p>
 * [一定线程安全吗](<a href=
 * "https://segmentfault.com/a/1190000018954561?utm_source=tag-newest">https://segmentfault.com/a/1190000018954561?utm_source=tag-newest</a>)
 *
 * @author kuma
 * @version 2022.07
 * @since 2022-07-08 08:46:29
 */
public class AttributeContext implements Attribute {

    /** 上下文 */
    private final Map<String, Object> context;

    public AttributeContext() {
        this.context = new ConcurrentHashMap<>();
    }

    public AttributeContext(final Map<String, Object> map) {
        this.context = new ConcurrentHashMap<>(map);
    }

    /**
     * 设置属性 map
     * @param map map 信息
     * @return this
     */
    protected AttributeContext putAttrMap(final Map<String, ?> map) {
        ArgUtils.notNull(map, "map");

        this.context.putAll(map);
        return this;
    }

    /**
     * 获取明细集合
     * @return 明细集合
     */
    protected Set<Map.Entry<String, Object>> entrySet() {
        return this.context.entrySet();
    }

    /**
     * 设置属性
     * @param key key
     * @param value 值
     * @return this
     */
    @Override
    public AttributeContext putAttr(final String key, final Object value) {
        context.put(key, value);
        return this;
    }

    /**
     * 获取配置属性
     * @return 目标对象
     */
    @Override
    public Object getAttr(final String key) {
        return context.get(key);
    }

    @Override
    public Optional<Object> getAttrOptional(String key) {
        Object object = getAttr(key);
        return Optional.ofNullable(object);
    }

    @Override
    public String getAttrString(String key) {
        Object object = getAttr(key);
        return ObjectUtils.objectToString(object);
    }

    @Override
    public Boolean getAttrBoolean(String key) {
        Optional<Object> objectOptional = getAttrOptional(key);
        return (Boolean) objectOptional.orElse(false);
    }

    @Override
    public Character getAttrCharacter(String key) {
        Optional<Object> objectOptional = getAttrOptional(key);
        assert objectOptional.orElse("") instanceof Character;
        return (Character) objectOptional.orElse("");
    }

    @Override
    public Byte getAttrByte(String key) {
        Optional<Object> objectOptional = getAttrOptional(key);
        assert objectOptional.orElse(0) instanceof Byte;
        return (Byte) objectOptional.orElse(0);
    }

    @Override
    public Short getAttrShort(String key) {
        Optional<Object> objectOptional = getAttrOptional(key);
        assert objectOptional.orElse(0) instanceof Short;
        return (Short) objectOptional.orElse(0);
    }

    @Override
    public Integer getAttrInteger(String key) {
        Optional<Object> objectOptional = getAttrOptional(key);
        return (Integer) objectOptional.orElse(0);
    }

    @Override
    public Float getAttrFloat(String key) {
        Optional<Object> objectOptional = getAttrOptional(key);
        assert objectOptional.orElse(0) instanceof Float;
        return (Float) objectOptional.orElse(0);
    }

    @Override
    public Double getAttrDouble(String key) {
        Optional<Object> objectOptional = getAttrOptional(key);
        assert objectOptional.orElse(0) instanceof Double;
        return (Double) objectOptional.orElse(0);
    }

    @Override
    public Long getAttrLong(String key) {
        Optional<Object> objectOptional = getAttrOptional(key);
        return (Long) objectOptional.orElse(0L);
    }

    @Override
    public Attribute removeAttr(String key) {
        context.remove(key);
        return this;
    }

    @Override
    public boolean containsKey(String key) {
        return context.containsKey(key);
    }

    @Override
    public Set<String> keySet() {
        return context.keySet();
    }
}
