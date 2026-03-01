/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Objects
 */
package com.kuma.boot.captcha.support.core.dto;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class GraphicCaptcha
extends Captcha {
    private String graphicImageBase64;

    public String getGraphicImageBase64() {
        return this.graphicImageBase64;
    }

    public void setGraphicImageBase64(String graphicImageBase64) {
        this.graphicImageBase64 = graphicImageBase64;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        GraphicCaptcha that = (GraphicCaptcha)o;
        return Objects.equal((Object)this.graphicImageBase64, (Object)that.graphicImageBase64);
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.graphicImageBase64});
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("graphicImageBase64", (Object)this.graphicImageBase64).toString();
    }
}

