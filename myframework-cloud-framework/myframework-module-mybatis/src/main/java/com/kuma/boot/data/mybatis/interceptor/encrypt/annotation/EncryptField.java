/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.data.mybatis.interceptor.encrypt.annotation;

import com.kuma.boot.data.mybatis.interceptor.encrypt.enumd.AlgorithmType;
import com.kuma.boot.data.mybatis.interceptor.encrypt.enumd.EncodeType;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段加密注解
 * <pre>{@code
 *        @Data
 * @EqualsAndHashCode(callSuper = true)
 * @TableName("test_demo")
 * public class TestDemoEncrypt extends TestDemo {
 *      @EncryptField(algorithm=AlgorithmType.SM2, privateKey = "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQgZSlOvw8FBiH+aFJWLYZP/VRjg9wjfRarTkGBZd/T3N+gCgYIKoEcz1UBgi2hRANCAAR5DGuQwJqkxnbCsP+iPSDoHWIF4RwcR5EsSvT8QPxO1wRkR2IhCkzvRb32x2CUgJFdvoqVqfApFDPZzShqzBwX", publicKey = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEeQxrkMCapMZ2wrD/oj0g6B1iBeEcHEeRLEr0/ED8TtcEZEdiIQpM70W99sdglICRXb6KlanwKRQz2c0oaswcFw==")
 * @EncryptField(algorithm = AlgorithmType.RSA, privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBANBBEeueWlXlkkj2+WY5l+IWe42d8b5K28g+G/CFKC/yYAEHtqGlCsBOrb+YBkG9mPzmuYA/n9k0NFIc8E8yY5vZQaroyFBrTTWEzG9RY2f7Y3svVyybs6jpXSUs4xff8abo7wL1Y/wUaeatTViamxYnyTvdTmLm3d+JjRij68rxAgMBAAECgYAB0TnhXraSopwIVRfmboea1b0upl+BUdTJcmci412UjrKr5aE695ZLPkXbFXijVu7HJlyyv94NVUdaMACV7Ku/S2RuNB70M7YJm8rAjHFC3/i2ZeIM60h1Ziy4QKv0XM3pRATlDCDNhC1WUrtQCQSgU8kcp6eUUppruOqDzcY04QJBAPm9+sBP9CwDRgy3e5+V8aZtJkwDstb0lVVV/KY890cydVxiCwvX3fqVnxKMlb+x0YtH0sb9v+71xvK2lGobaRECQQDVePU6r/cCEfpc+nkWF6osAH1f8Mux3rYv2DoBGvaPzV2BGfsLed4neRfCwWNCKvGPCdW+L0xMJg8+RwaoBUPhAkAT5kViqXxFPYWJYd1h2+rDXhMdH3ZSlm6HvDBDdrwlWinr0Iwcx3iSjPV93uHXwm118aUj4fg3LDJMCKxOwBxhAkByrQXfvwOMYygBprRBf/j0plazoWFrbd6lGR0f1uI5IfNnFRPdeFw1DEINZ2Hw+6zEUF44SqRMC+4IYJNc02dBAkBCgy7RvfyV/A7N6kKXxTHauY0v6XwSSvpeKtRJkbIcRWOdIYvaHO9L7cklj3vIEdwjSUp9K4VTBYYlmAz1xh03", publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDQQRHrnlpV5ZJI9vlmOZfiFnuNnfG+StvIPhvwhSgv8mABB7ahpQrATq2/mAZBvZj85rmAP5/ZNDRSHPBPMmOb2UGq6MhQa001hMxvUWNn+2N7L1csm7Oo6V0lLOMX3/Gm6O8C9WP8FGnmrU1YmpsWJ8k73U5i5t3fiY0Yo+vK8QIDAQAB")
 * private String testKey;
 *     // @EncryptField // 什么也不写走默认yml配置
 *     // @EncryptField(algorithm = AlgorithmType.SM4, password = "10rfylhtccpuyke5")
 * @EncryptField(algorithm = AlgorithmType.AES, password = "10rfylhtccpuyke5")
 * private String value;
 *  }
 *  }
 * </pre>
 */
@Documented
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EncryptField {

    /**
     * 加密算法
     */
    AlgorithmType algorithm() default AlgorithmType.DEFAULT;

    /**
     * 秘钥。AES、SM4需要
     */
    String password() default "";

    /**
     * 公钥。RSA、SM2需要
     */
    String publicKey() default "";

    /**
     * 公钥。RSA、SM2需要
     */
    String privateKey() default "";

    /**
     * 编码方式。对加密算法为BASE64的不起作用
     */
    EncodeType encode() default EncodeType.DEFAULT;
}
