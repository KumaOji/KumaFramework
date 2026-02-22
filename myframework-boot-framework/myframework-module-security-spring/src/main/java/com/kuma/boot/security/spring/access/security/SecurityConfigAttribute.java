/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Objects
 *  org.springframework.security.access.ConfigAttribute
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package com.kuma.boot.security.spring.access.security;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class SecurityConfigAttribute
implements ConfigAttribute {
    private String attribute;

    public SecurityConfigAttribute() {
    }

    public SecurityConfigAttribute(String config) {
        Assert.hasText((String)config, (String)"You must provide a configuration attribute");
        this.attribute = config;
    }

    public String getAttribute() {
        return this.attribute;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SecurityConfigAttribute that = (SecurityConfigAttribute)o;
        return Objects.equal((Object)this.attribute, (Object)that.attribute);
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.attribute});
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("attrib", (Object)this.attribute).toString();
    }

    public static SecurityConfigAttribute create(String attribute) {
        Assert.notNull((Object)attribute, (String)"You must supply an array of attribute names");
        return new SecurityConfigAttribute(attribute.trim());
    }

    public static List<ConfigAttribute> createListFromCommaDelimitedString(String access) {
        return SecurityConfigAttribute.createList(StringUtils.commaDelimitedListToStringArray((String)access));
    }

    public static List<ConfigAttribute> createList(String ... attributeNames) {
        Assert.notNull((Object)attributeNames, (String)"You must supply an array of attribute names");
        ArrayList<ConfigAttribute> attributes = new ArrayList<ConfigAttribute>(attributeNames.length);
        for (String attribute : attributeNames) {
            attributes.add(new SecurityConfigAttribute(attribute.trim()));
        }
        return attributes;
    }
}

