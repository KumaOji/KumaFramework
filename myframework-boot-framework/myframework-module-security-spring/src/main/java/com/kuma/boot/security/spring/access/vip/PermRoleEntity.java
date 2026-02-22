/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.security.access.ConfigAttribute
 */
package com.kuma.boot.security.spring.access.vip;

import java.util.List;
import java.util.Objects;
import org.springframework.security.access.ConfigAttribute;

public class PermRoleEntity {
    private String accessUri;
    private List<ConfigAttribute> configAttributeList;

    public String getAccessUri() {
        return this.accessUri;
    }

    public void setAccessUri(String accessUri) {
        this.accessUri = accessUri;
    }

    public List<ConfigAttribute> getConfigAttributeList() {
        return this.configAttributeList;
    }

    public void setConfigAttributeList(List<ConfigAttribute> configAttributeList) {
        this.configAttributeList = configAttributeList;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PermRoleEntity that = (PermRoleEntity)o;
        if (!Objects.equals(this.accessUri, that.accessUri)) {
            return false;
        }
        return Objects.equals(this.configAttributeList, that.configAttributeList);
    }

    public int hashCode() {
        int result = this.accessUri != null ? this.accessUri.hashCode() : 0;
        result = 31 * result + (this.configAttributeList != null ? this.configAttributeList.hashCode() : 0);
        return result;
    }
}

