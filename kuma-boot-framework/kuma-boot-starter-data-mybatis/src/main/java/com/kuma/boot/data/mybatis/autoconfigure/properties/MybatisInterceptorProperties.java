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

/**
 * MybatisPlusAutoFillProperties
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-04 07:44:25
 */
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
     * sql鏃ュ織鎻掍欢
     */
    private SqlLog sqlLog;

    /**
     * sql鏀堕泦鎻掍欢
     */
    private SqlCollector sqlCollector;

    /**
     * 瀛楁鍔犲瘑鎻掍欢
     */
    private CipherEncrypt cipherEncrypt;

    /**
     * 瀛楁鍔犲瘑鎻掍欢
     */
    private FieldEncrypt fieldEncrypt;

    /**
     * 鏁版嵁鎿嶄綔鎻掍欢
     */
    private DataOperate dataOperate;

    /**
     * 鎱㈡煡璇㈡彃浠?
     */
    private SlowQuery slowQuery;

    /**
     * 澶х粨鏋滈泦鍚堟彃浠?
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
         * 杩囨护寮€鍏?
         */
        private Boolean enabled = false;

        /**
         * 榛樿绠楁硶
         */
        private AlgorithmType algorithm;

        /**
         * 绉橀挜銆侫ES銆丼M4闇€瑕?
         */
        private String password;

        /**
         * 鍏挜銆俁SA銆丼M2闇€瑕?
         */
        private String publicKey;

        /**
         * 鍏挜銆俁SA銆丼M2闇€瑕?
         */
        private String privateKey;

        /**
         * 缂栫爜鏂瑰紡銆傚鍔犲瘑绠楁硶涓築ASE64鐨勪笉璧蜂綔鐢?
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
         * 鏄惁寮€鍚參鏌ヨ鐩戞帶
         */
        private Boolean enabled = false;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled( Boolean enabled ) {
            this.enabled = enabled;
        }

        /**
         * 鏈€澶ч槇鍊?
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
         * 鏄惁寮€鍚ぇ鏌ヨ鐩戞帶
         */
        private Boolean enabled = false;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled( Boolean enabled ) {
            this.enabled = enabled;
        }

        /**
         * 鏈€澶ч槇鍊?
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
