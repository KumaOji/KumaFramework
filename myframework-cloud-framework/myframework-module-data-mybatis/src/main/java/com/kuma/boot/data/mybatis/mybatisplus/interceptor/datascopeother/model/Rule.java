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

package com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascopeother.model;

// @TableName("sys_rule")
/**
 * Rule
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class Rule {

    private String remark;

    private Long markId;

    private String tableAlias;

    private String columnName;

    private String spliceType;

    private String expression;

    private Integer provideType;

    private String value1;

    private String value2;

    private String className;

    private String methodName;

    private String formalParam;

    private String actualParam;

    public String getRemark() {
        return remark;
    }

    public void setRemark( String remark ) {
        this.remark = remark;
    }

    public Long getMarkId() {
        return markId;
    }

    public void setMarkId( Long markId ) {
        this.markId = markId;
    }

    public String getTableAlias() {
        return tableAlias;
    }

    public void setTableAlias( String tableAlias ) {
        this.tableAlias = tableAlias;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName( String columnName ) {
        this.columnName = columnName;
    }

    public String getSpliceType() {
        return spliceType;
    }

    public void setSpliceType( String spliceType ) {
        this.spliceType = spliceType;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression( String expression ) {
        this.expression = expression;
    }

    public Integer getProvideType() {
        return provideType;
    }

    public void setProvideType( Integer provideType ) {
        this.provideType = provideType;
    }

    public String getValue1() {
        return value1;
    }

    public void setValue1( String value1 ) {
        this.value1 = value1;
    }

    public String getValue2() {
        return value2;
    }

    public void setValue2( String value2 ) {
        this.value2 = value2;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName( String className ) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName( String methodName ) {
        this.methodName = methodName;
    }

    public String getFormalParam() {
        return formalParam;
    }

    public void setFormalParam( String formalParam ) {
        this.formalParam = formalParam;
    }

    public String getActualParam() {
        return actualParam;
    }

    public void setActualParam( String actualParam ) {
        this.actualParam = actualParam;
    }
}
