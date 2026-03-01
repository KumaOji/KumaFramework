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

package com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascopeother.model;

import java.util.List;

/**
 * RuleDto
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class RuleDto extends com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascopeother.model.EntityDto {

    //    @NotNull(message = "markId不能为空", groups = {CreateGroup.class, UpdateGroup.class})
    //    @ApiModelProperty(value = "markId", required = true)
    //    @JsonSerialize(using = LongSerializer.class)
    private Long markId;

    //    @ApiModelProperty("备注")
    private String remark;

    //    @ApiModelProperty("表别名")
    private String tableAlias;

    //    @NotNull(message = "字段名不能为空", groups = {CreateGroup.class, UpdateGroup.class})
    //    @ApiModelProperty(value = "字段名", required = true)
    private String columnName;

    //    @NotNull(message = "拼接类型不能为空", groups = {CreateGroup.class, UpdateGroup.class})
    //    @ApiModelProperty(value = "拼接类型 OR AND", required = true)
    private String spliceType;

    //    @NotNull(message = "表达式 EQ NE LE GT...", groups = {CreateGroup.class, UpdateGroup.class})
    //    @ApiModelProperty(value = "表达式 EQ NE LE GT...", required = true)
    private String expression;

    //    @NotNull(message = "提供类型", groups = {CreateGroup.class, UpdateGroup.class})
    //    @ApiModelProperty(value = "提供类型", required = true)
    private Integer provideType;

    //    @ApiModelProperty("值1")
    private String value1;

    //    @ApiModelProperty("值2")
    private String value2;

    //    @ApiModelProperty("全限定类名")
    private String className;

    //    @ApiModelProperty("方法名")
    private String methodName;

    //    @ApiModelProperty("形参")
    private String formalParam;

    //    @ApiModelProperty("实参注入")
    private String actualParam;

    //    @ApiModelProperty("参数")
    private List<com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascopeother.model.ParamDto> paramList;

    public Long getMarkId() {
        return markId;
    }

    public void setMarkId( Long markId ) {
        this.markId = markId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark( String remark ) {
        this.remark = remark;
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

    public List<com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascopeother.model.ParamDto> getParamList() {
        return paramList;
    }

    public void setParamList( List<com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascopeother.model.ParamDto> paramList ) {
        this.paramList = paramList;
    }
}
