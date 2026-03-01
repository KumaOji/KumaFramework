//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.security.spring.access.security;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.springframework.util.Assert;

public class SecurityConfigAttribute {
    private String attribute;

    public SecurityConfigAttribute() {
    }

    public SecurityConfigAttribute(String config) {
        Assert.hasText(config, "You must provide a configuration attribute");
        this.attribute = config;
    }

    public String getAttribute() {
        return this.attribute;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            SecurityConfigAttribute that = (SecurityConfigAttribute)o;
            return Objects.equal(this.attribute, that.attribute);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hashCode(new Object[]{this.attribute});
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("attrib", this.attribute).toString();
    }

    public static SecurityConfigAttribute create(String attribute) {
        Assert.notNull(attribute, "You must supply an array of attribute names");
        return new SecurityConfigAttribute(attribute.trim());
    }
}
