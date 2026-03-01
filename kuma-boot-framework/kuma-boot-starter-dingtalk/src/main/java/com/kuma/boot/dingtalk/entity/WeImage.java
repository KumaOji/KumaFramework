/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.entity;

import com.kuma.boot.dingtalk.enums.WeTalkMsgType;
import java.io.Serializable;

public class WeImage
extends WeTalkMessage {
    private Image image;

    public WeImage() {
        this.setMsgtype(WeTalkMsgType.IMAGE.type());
    }

    public Image getImage() {
        return this.image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public static class Image
    implements Serializable {
        private String base64;
        private String md5;

        public String getBase64() {
            return this.base64;
        }

        public void setBase64(String base64) {
            this.base64 = base64;
        }

        public String getMd5() {
            return this.md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }
    }
}

