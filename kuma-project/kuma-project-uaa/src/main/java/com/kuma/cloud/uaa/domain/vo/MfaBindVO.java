package com.kuma.cloud.uaa.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class MfaBindVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Base32 共享密钥，供无法扫码时手工录入 */
    private String secret;

    /** otpauth:// 链接 */
    private String otpAuthUri;

    /** data URI 形式的二维码 PNG */
    private String qrImage;
}
