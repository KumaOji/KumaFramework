/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.attr;

import java.util.Optional;
import java.util.Set;

public interface Attribute {
    public Attribute putAttr(String var1, Object var2);

    public Object getAttr(String var1);

    public Optional<Object> getAttrOptional(String var1);

    public String getAttrString(String var1);

    public Boolean getAttrBoolean(String var1);

    public Character getAttrCharacter(String var1);

    public Byte getAttrByte(String var1);

    public Short getAttrShort(String var1);

    public Integer getAttrInteger(String var1);

    public Float getAttrFloat(String var1);

    public Double getAttrDouble(String var1);

    public Long getAttrLong(String var1);

    public Attribute removeAttr(String var1);

    public boolean containsKey(String var1);

    public Set<String> keySet();
}

