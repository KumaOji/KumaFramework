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

package com.kuma.boot.data.mybatis.autoconfigure.properties;

import com.kuma.boot.data.mybatis.interceptor.encrypt.enumd.AlgorithmType;
import com.kuma.boot.data.mybatis.interceptor.encrypt.enumd.EncodeType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * MybatisPlusAutoFillProperties
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-04 07:44:25
 */
@RefreshScope
@ConfigurationProperties(prefix = MybatisInterceptorProperties.PREFIX)
public class MybatisInterceptorProperties {

    public static final String PREFIX = "kuma.boot.data.mybatis.interceptor";

    public static final String SQL_LOG_PREFIX = PREFIX + ".sql-log";
    public static final String SQL_COLLECTOR_PREFIX = PREFIX + ".sql-collector";
    public static final String CIPHER_ENCRYPT_PREFIX = PREFIX + ".cipher-encrypt";
    public static final String FIELD_ENCRYPT_PREFIX = PREFIX + ".field-encrypt";
    public static final String DATA_OPERATE_PREFIX = PREFIX + ".data-operate";
    public static final String BIG_RESULT_QUERY_PREFIX = PREFIX + ".big-result-query";

    private Boolean enabled = true;

    /**
     * sql日志插件
     */
    private SqlLog sqlLog;

    /**
     * sql收集插件
     */
    private SqlCollector sqlCollector;

    /**
     * 字段加密插件
     */
    private CipherEncrypt cipherEncrypt;

    /**
     * 字段加密插件
     */
    private FieldEncrypt fieldEncrypt;

    /**
     * 数据操作插件
     */
    private DataOperate dataOperate;

    /**
     * 慢查询插件
     */
    private SlowQuery slowQuery;

    /**
     * 大结果集合插件
     */
    private BigResultQuery bigResultQuery;

    /**
     * SqlLog
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    public static class SqlLog {

        private Boolean enabled = false;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled( Boolean enabled ) {
            this.enabled = enabled;
        }
    }

    /**
     * SqlCollector
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    public static class SqlCollector {

        private Boolean enabled = false;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled( Boolean enabled ) {
            this.enabled = enabled;
        }
    }

    /**
     * FieldEncrypt
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    public static class FieldEncrypt {

        /**
         * 过滤开关
         */
        private Boolean enabled = false;

        /**
         * 默认算法
         */
        private AlgorithmType algorithm;

        /**
         * 秘钥。AES、SM4需要
         */
        private String password;

        /**
         * 公钥。RSA、SM2需要
         */
        private String publicKey;

        /**
         * 公钥。RSA、SM2需要
         */
        private String privateKey;

        /**
         * 编码方式。对加密算法为BASE64的不起作用
         */
        private EncodeType encode;

        public AlgorithmType getAlgorithm() {
            return algorithm;
        }

        public void setAlgorithm( AlgorithmType algorithm ) {
            this.algorithm = algorithm;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword( String password ) {
            this.password = password;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public void setPublicKey( String publicKey ) {
            this.publicKey = publicKey;
        }

        public String getPrivateKey() {
            return privateKey;
        }

        public void setPrivateKey( String privateKey ) {
            this.privateKey = privateKey;
        }

        public EncodeType getEncode() {
            return encode;
        }

        public void setEncode( EncodeType encode ) {
            this.encode = encode;
        }

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled( Boolean enabled ) {
            this.enabled = enabled;
        }
    }

    /**
     * CipherEncrypt
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    public static class CipherEncrypt {

        private Boolean enabled = false;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled( Boolean enabled ) {
            this.enabled = enabled;
        }
    }

    /**
     * DataOperate
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    public static class DataOperate {

        private Boolean enabled = false;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled( Boolean enabled ) {
            this.enabled = enabled;
        }
    }

    /**
     * SlowQuery
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    public static class SlowQuery {

        /**
         * 是否开启慢查询监控
         */
        private Boolean enabled = false;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled( Boolean enabled ) {
            this.enabled = enabled;
        }

        /**
         * 最大阈值
         */
        private Integer slowSqlThresholdMs = 6000;

        public Integer getSlowSqlThresholdMs() {
            return slowSqlThresholdMs;
        }

        public void setSlowSqlThresholdMs( Integer slowSqlThresholdMs ) {
            this.slowSqlThresholdMs = slowSqlThresholdMs;
        }
    }

    /**
     * BigResultQuery
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    public static class BigResultQuery {

        /**
         * 是否开启大查询监控
         */
        private Boolean enabled = false;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled( Boolean enabled ) {
            this.enabled = enabled;
        }

        /**
         * 最大阈值
         */
        private Integer size = 1000;

        public Integer getSize() {
            return size;
        }

        public void setSize( Integer size ) {
            this.size = size;
        }
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled( Boolean enabled ) {
        this.enabled = enabled;
    }

    public SqlLog getSqlLog() {
        return sqlLog;
    }

    public void setSqlLog( SqlLog sqlLog ) {
        this.sqlLog = sqlLog;
    }

    public SqlCollector getSqlCollector() {
        return sqlCollector;
    }

    public void setSqlCollector( SqlCollector sqlCollector ) {
        this.sqlCollector = sqlCollector;
    }

    public FieldEncrypt getFieldEncrypt() {
        return fieldEncrypt;
    }

    public void setFieldEncrypt( FieldEncrypt fieldEncrypt ) {
        this.fieldEncrypt = fieldEncrypt;
    }

    public DataOperate getDataOperate() {
        return dataOperate;
    }

    public void setDataOperate( DataOperate dataOperate ) {
        this.dataOperate = dataOperate;
    }

    public SlowQuery getSlowQuery() {
        return slowQuery;
    }

    public void setSlowQuery( SlowQuery slowQuery ) {
        this.slowQuery = slowQuery;
    }

    public BigResultQuery getBigResultQuery() {
        return bigResultQuery;
    }

    public void setBigResultQuery( BigResultQuery bigResultQuery ) {
        this.bigResultQuery = bigResultQuery;
    }

    public CipherEncrypt getCipherEncrypt() {
        return cipherEncrypt;
    }

    public void setCipherEncrypt( CipherEncrypt cipherEncrypt ) {
        this.cipherEncrypt = cipherEncrypt;
    }
}
