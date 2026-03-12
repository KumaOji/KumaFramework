//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.support.core.dto;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class GraphicCaptcha extends Captcha {
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
        } else if (o != null && this.getClass() == o.getClass()) {
            GraphicCaptcha that = (GraphicCaptcha)o;
            return Objects.equal(this.graphicImageBase64, that.graphicImageBase64);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hashCode(new Object[]{this.graphicImageBase64});
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("graphicImageBase64", this.graphicImageBase64).toString();
    }
}
