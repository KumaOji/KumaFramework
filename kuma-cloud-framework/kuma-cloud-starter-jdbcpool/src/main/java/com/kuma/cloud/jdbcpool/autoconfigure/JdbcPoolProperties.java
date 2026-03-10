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

package com.kuma.cloud.jdbcpool.autoconfigure;

import com.kuma.cloud.jdbcpool.constant.DriverNameConst;
import com.kuma.cloud.jdbcpool.constant.PooledConst;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JdbcPool 配置属性
 *
 * @author kuma
 * @since 1.8.0
 */
@ConfigurationProperties(prefix = "kuma.jdbcpool")
public class JdbcPoolProperties {

    /** 驱动类 */
    private String driverClass = DriverNameConst.MYSQL_8;

    /** JDBC URL */
    private String url = "jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC";

    /** 用户名 */
    private String username = "root";

    /** 密码 */
    private String password = "123456";

    /** 最小连接数 */
    private int minSize = PooledConst.DEFAULT_MIN_SIZE;

    /** 最大连接数 */
    private int maxSize = PooledConst.DEFAULT_MAX_SIZE;

    /** 最大等待时间（毫秒） */
    private long maxWaitMills = PooledConst.DEFAULT_MAX_WAIT_MILLS;

    /** 验证查询语句 */
    private String validQuery = PooledConst.DEFAULT_VALID_QUERY;

    /** 验证超时时间（秒） */
    private int validTimeOutSeconds = PooledConst.DEFAULT_VALID_TIME_OUT_SECONDS;

    /** 获取连接时验证 */
    private boolean testOnBorrow = PooledConst.DEFAULT_TEST_ON_BORROW;

    /** 归还连接时验证 */
    private boolean testOnReturn = PooledConst.DEFAULT_TEST_ON_RETURN;

    /** 闲暇时验证 */
    private boolean testOnIdle = PooledConst.DEFAULT_TEST_ON_IDLE;

    /** 闲暇验证时间间隔（秒） */
    private long testOnIdleIntervalSeconds = PooledConst.DEFAULT_TEST_ON_IDLE_INTERVAL_SECONDS;

    public String getDriverClass() { return driverClass; }
    public void setDriverClass(String driverClass) { this.driverClass = driverClass; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getMinSize() { return minSize; }
    public void setMinSize(int minSize) { this.minSize = minSize; }

    public int getMaxSize() { return maxSize; }
    public void setMaxSize(int maxSize) { this.maxSize = maxSize; }

    public long getMaxWaitMills() { return maxWaitMills; }
    public void setMaxWaitMills(long maxWaitMills) { this.maxWaitMills = maxWaitMills; }

    public String getValidQuery() { return validQuery; }
    public void setValidQuery(String validQuery) { this.validQuery = validQuery; }

    public int getValidTimeOutSeconds() { return validTimeOutSeconds; }
    public void setValidTimeOutSeconds(int validTimeOutSeconds) { this.validTimeOutSeconds = validTimeOutSeconds; }

    public boolean isTestOnBorrow() { return testOnBorrow; }
    public void setTestOnBorrow(boolean testOnBorrow) { this.testOnBorrow = testOnBorrow; }

    public boolean isTestOnReturn() { return testOnReturn; }
    public void setTestOnReturn(boolean testOnReturn) { this.testOnReturn = testOnReturn; }

    public boolean isTestOnIdle() { return testOnIdle; }
    public void setTestOnIdle(boolean testOnIdle) { this.testOnIdle = testOnIdle; }

    public long getTestOnIdleIntervalSeconds() { return testOnIdleIntervalSeconds; }
    public void setTestOnIdleIntervalSeconds(long testOnIdleIntervalSeconds) { this.testOnIdleIntervalSeconds = testOnIdleIntervalSeconds; }
}
