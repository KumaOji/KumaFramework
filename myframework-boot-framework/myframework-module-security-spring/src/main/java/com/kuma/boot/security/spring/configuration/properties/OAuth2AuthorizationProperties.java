/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.security.spring.configuration.properties;

import com.google.common.base.MoreObjects;
import com.kuma.boot.security.spring.enums.Certificate;
import com.kuma.boot.security.spring.enums.Target;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>OAuth2 授权配置属性
 * <p>
 * 仅认证服务会使用到授权安全相关配置，这是与 OAuth2Properties 的主要区别。
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-04 10:01:56
 */
@ConfigurationProperties(prefix = OAuth2AuthorizationProperties.PREFIX)
public class OAuth2AuthorizationProperties {

    public static final String PREFIX = "kuma.boot.security.oauth2.authorization";

    /**
     * Token 校验是采用远程方式还是本地方式。
     */
    private Target validate = Target.LOCAL;

    /**
     * 是否使用严格模式。严格模式一定要求有权限，非严格模式没有权限管控的接口，只要认证通过也可以使用。
     */
    private Boolean strict = false;

    /**
     * JWT的密钥或者密钥对(JSON Web Key) 配置
     */
    private Jwk jwk = new Jwk();

    /**
     * 指定 Request Matcher 静态安全规则
     */
    private Matcher matcher = new Matcher();

    /**
     * 得到验证
     *
     * @return {@link Target }
     * @since 2023-07-04 10:01:56
     */
    public Target getValidate() {
        return validate;
    }

    /**
     * 设置验证
     *
     * @param validate 验证
     * @since 2023-07-04 10:01:56
     */
    public void setValidate(Target validate) {
        this.validate = validate;
    }

    /**
     * 得到jwk
     *
     * @return {@link Jwk }
     * @since 2023-07-04 10:01:56
     */
    public Jwk getJwk() {
        return jwk;
    }

    /**
     * 设置jwk
     *
     * @param jwk jwk
     * @since 2023-07-04 10:01:56
     */
    public void setJwk(Jwk jwk) {
        this.jwk = jwk;
    }

    /**
     * 得到匹配器
     *
     * @return {@link Matcher }
     * @since 2023-07-04 10:01:57
     */
    public Matcher getMatcher() {
        return matcher;
    }

    /**
     * 匹配器设置
     *
     * @param matcher 匹配器
     * @since 2023-07-04 10:01:57
     */
    public void setMatcher(Matcher matcher) {
        this.matcher = matcher;
    }

    /**
     * 得到严格
     *
     * @return {@link Boolean }
     * @since 2023-07-04 10:01:57
     */
    public Boolean getStrict() {
        return strict;
    }

    /**
     * 设置严格
     *
     * @param strict 严格
     * @since 2023-07-04 10:01:57
     */
    public void setStrict(Boolean strict) {
        this.strict = strict;
    }

    /**
     * 1.生成私钥公钥 Keytool 是一个java提供的证书管理工具 keytool -genkeypair -alias oyjp -keyalg RSA -keypass
     * ouyangjianpeng -keystore ouyangjianpeng.jks -storepass ouyangjianpeng //-alias：密钥的别名
     * //-keyalg：使用的hash算法 //-keypass：密钥的访问密码 //-keystore：密钥库文件名，kmc.jks保存了生成的证书
     * //-storepass：密钥库的访问密码 keytool -genkeypair -alias kmc -keyalg RSA -keypass
     * kmc -validity 36500 -keystore kmc.jks -storepass kmc
     * 成功后会在当前目录生成一个ouyangjianpeng.jks文件
     * <p>
     * keytool -delete -alias kmc -keystore kmc.jks
     * <p>
     * 查询证书信息： keytool -list -keystore kmc.jks
     * <p>
     * 导出公钥 keytool -list -rfc --keystore kmc.jks | openssl x509 -inform pem -pubkey
     *
     * @author kuma
     * @version 2023.07
     * @since 2023-07-04 10:01:57
     */
    public static class Jwk {

        /**
         * 证书策略：standard OAuth2 标准证书模式；custom 自定义证书模式
         */
        private Certificate certificate = Certificate.STANDARD;

        /**
         * jks证书文件路径
         */
        private String jksKeyStore = "classpath*:certificate/kmc.jks";

        /**
         * jks证书密码
         */
        private String jksKeyPassword = "kmc";

        /**
         * jks证书密钥库密码
         */
        private String jksStorePassword = "kmc";

        /**
         * jks证书别名
         */
        private String jksKeyAlias = "kmc";

        /**
         * 获得证书
         *
         * @return {@link Certificate }
         * @since 2023-07-04 10:01:57
         */
        public Certificate getCertificate() {
            return certificate;
        }

        /**
         * 设置证书
         *
         * @param certificate 证书
         * @since 2023-07-04 10:01:57
         */
        public void setCertificate(Certificate certificate) {
            this.certificate = certificate;
        }

        /**
         * 得到jks密钥存储
         *
         * @return {@link String }
         * @since 2023-07-04 10:01:57
         */
        public String getJksKeyStore() {
            return jksKeyStore;
        }

        /**
         * 设置jks密钥存储
         *
         * @param jksKeyStore jks密钥存储
         * @since 2023-07-04 10:01:58
         */
        public void setJksKeyStore(String jksKeyStore) {
            this.jksKeyStore = jksKeyStore;
        }

        /**
         * 得到jks关键密码
         *
         * @return {@link String }
         * @since 2023-07-04 10:01:58
         */
        public String getJksKeyPassword() {
            return jksKeyPassword;
        }

        /**
         * 设置jks关键密码
         *
         * @param jksKeyPassword jks关键密码
         * @since 2023-07-04 10:01:58
         */
        public void setJksKeyPassword(String jksKeyPassword) {
            this.jksKeyPassword = jksKeyPassword;
        }

        /**
         * 得到jks存储密码
         *
         * @return {@link String }
         * @since 2023-07-04 10:01:58
         */
        public String getJksStorePassword() {
            return jksStorePassword;
        }

        /**
         * 设置jks存储密码
         *
         * @param jksStorePassword jks存储密码
         * @since 2023-07-04 10:01:58
         */
        public void setJksStorePassword(String jksStorePassword) {
            this.jksStorePassword = jksStorePassword;
        }

        /**
         * 得到jks关键别名
         *
         * @return {@link String }
         * @since 2023-07-04 10:01:58
         */
        public String getJksKeyAlias() {
            return jksKeyAlias;
        }

        /**
         * 设置jks关键别名
         *
         * @param jksKeyAlias jks关键别名
         * @since 2023-07-04 10:01:59
         */
        public void setJksKeyAlias(String jksKeyAlias) {
            this.jksKeyAlias = jksKeyAlias;
        }

        /**
         * 字符串
         *
         * @return {@link String }
         * @since 2023-07-04 10:01:59
         */
        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("certificate", certificate)
                    .add("jksKeyStore", jksKeyStore)
                    .add("jksKeyPassword", jksKeyPassword)
                    .add("jksStorePassword", jksStorePassword)
                    .add("jksKeyAlias", jksKeyAlias)
                    .toString();
        }
    }

    /**
     * 用于手动的指定 Request Matcher 安全规则。
     * <p>
     * permitAll 比较常用，因此先只增加该项。后续可根据需要添加
     */
    public static class Matcher {

        /**
         * 静态资源过滤
         */
        private List<String> staticResources;

        /**
         * Security "permitAll" 权限列表。
         */
        private List<String> permitAll;

        /**
         * 只校验是否请求中包含Token，不校验Token中是否包含该权限的资源
         */
        private List<String> hasAuthenticated;

        public List<String> getStaticResources() {
            return staticResources;
        }

        public void setStaticResources(List<String> staticResources) {
            this.staticResources = staticResources;
        }

        public List<String> getPermitAll() {
            return permitAll;
        }

        public void setPermitAll(List<String> permitAll) {
            this.permitAll = permitAll;
        }

        public List<String> getHasAuthenticated() {
            return hasAuthenticated;
        }

        public void setHasAuthenticated(List<String> hasAuthenticated) {
            this.hasAuthenticated = hasAuthenticated;
        }
    }
}
