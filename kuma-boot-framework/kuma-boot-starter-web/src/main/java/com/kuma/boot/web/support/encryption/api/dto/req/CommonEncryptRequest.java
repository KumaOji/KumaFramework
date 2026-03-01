package com.kuma.boot.web.support.encryption.api.dto.req;


import com.kuma.boot.web.support.encryption.api.core.EncryptMask;

/**
 * 加密入参
 *
 * @author binbin.hou
 * @since 1.0.0
 */
public class CommonEncryptRequest extends CommonRequest {

    /**
     * 待加密内容
     */
    private String text;

    /**
     * 加密策略
     *
     * @since 1.2.0
     */
    private EncryptMask encryptMask;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public EncryptMask getEncryptMask() {
        return encryptMask;
    }

    public void setEncryptMask( EncryptMask encryptMask) {
        this.encryptMask = encryptMask;
    }
}
