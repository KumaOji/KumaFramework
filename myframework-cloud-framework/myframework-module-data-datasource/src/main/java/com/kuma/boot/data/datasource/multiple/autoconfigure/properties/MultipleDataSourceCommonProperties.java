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

package com.kuma.boot.data.datasource.multiple.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ExtDataSourceCommonProperties
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
@ConfigurationProperties(prefix = MultipleDataSourceCommonProperties.PREFIX, ignoreUnknownFields = false)
public class MultipleDataSourceCommonProperties {

    static final String PREFIX = "kuma.boot.data.datasource.multiple.common";
    private int initialSize = 10;
    private int minIdle;
    private int maxIdle;
    private int maxActive;
    private int maxWait;
    private int timeBetweenEvictionRunsMillis;
    private int minEvictableIdleTimeMillis;
    private String validationQuery;
    private boolean testWhileIdle;
    private boolean testOnBorrow;
    private boolean testOnReturn;
    private boolean poolPreparedStatements;
    private int maxOpenPreparedStatements;
    private String filters;
    private String mapperLocations;
    private String typeAliasPackage;

    public int getInitialSize() {
        return initialSize;
    }

    public void setInitialSize( int initialSize ) {
        this.initialSize = initialSize;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle( int minIdle ) {
        this.minIdle = minIdle;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle( int maxIdle ) {
        this.maxIdle = maxIdle;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive( int maxActive ) {
        this.maxActive = maxActive;
    }

    public int getMaxWait() {
        return maxWait;
    }

    public void setMaxWait( int maxWait ) {
        this.maxWait = maxWait;
    }

    public int getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public void setTimeBetweenEvictionRunsMillis( int timeBetweenEvictionRunsMillis ) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public int getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public void setMinEvictableIdleTimeMillis( int minEvictableIdleTimeMillis ) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public String getValidationQuery() {
        return validationQuery;
    }

    public void setValidationQuery( String validationQuery ) {
        this.validationQuery = validationQuery;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle( boolean testWhileIdle ) {
        this.testWhileIdle = testWhileIdle;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow( boolean testOnBorrow ) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn( boolean testOnReturn ) {
        this.testOnReturn = testOnReturn;
    }

    public boolean isPoolPreparedStatements() {
        return poolPreparedStatements;
    }

    public void setPoolPreparedStatements( boolean poolPreparedStatements ) {
        this.poolPreparedStatements = poolPreparedStatements;
    }

    public int getMaxOpenPreparedStatements() {
        return maxOpenPreparedStatements;
    }

    public void setMaxOpenPreparedStatements( int maxOpenPreparedStatements ) {
        this.maxOpenPreparedStatements = maxOpenPreparedStatements;
    }

    public String getFilters() {
        return filters;
    }

    public void setFilters( String filters ) {
        this.filters = filters;
    }

    public String getMapperLocations() {
        return mapperLocations;
    }

    public void setMapperLocations( String mapperLocations ) {
        this.mapperLocations = mapperLocations;
    }

    public String getTypeAliasPackage() {
        return typeAliasPackage;
    }

    public void setTypeAliasPackage( String typeAliasPackage ) {
        this.typeAliasPackage = typeAliasPackage;
    }
}
