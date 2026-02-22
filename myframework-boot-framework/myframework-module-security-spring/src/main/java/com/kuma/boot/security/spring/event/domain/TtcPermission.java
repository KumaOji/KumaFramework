/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Objects
 *  io.swagger.v3.oas.annotations.media.Schema
 */
package com.kuma.boot.security.spring.event.domain;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name="\u7cfb\u7edf\u6743\u9650")
public class TtcPermission {
    @Schema(name="\u6743\u9650ID")
    private String permissionId;
    @Schema(name="\u6743\u9650\u4ee3\u7801")
    private String permissionCode;
    @Schema(name="\u6743\u9650\u540d\u79f0")
    private String permissionName;

    public String getPermissionId() {
        return this.permissionId;
    }

    public void setPermissionId(String permissionId) {
        this.permissionId = permissionId;
    }

    public String getPermissionCode() {
        return this.permissionCode;
    }

    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }

    public String getPermissionName() {
        return this.permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        TtcPermission that = (TtcPermission)o;
        return Objects.equal((Object)this.permissionId, (Object)that.permissionId);
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.permissionId});
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("permissionId", (Object)this.permissionId).add("permissionCode", (Object)this.permissionCode).add("permissionName", (Object)this.permissionName).toString();
    }
}

