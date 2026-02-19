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

package com.kuma.boot.data.mybatis.mybatisplus.aggregate.query.entity;

import com.kuma.boot.data.mybatis.mybatisplus.aggregate.query.code.CompareTypeEnum;
import com.kuma.boot.data.mybatis.mybatisplus.aggregate.query.code.ParamTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/** */
@Schema(title = "查询项")
public class QueryParam {

    @Schema(description = "拼接条件是否为或")
    private boolean or;

    @Schema(description = "参数名称")
    private String paramName;

    /**
     * @see CompareTypeEnum
     */
    @Schema(description = "比较类型")
    private String compareType;

    /**
     * @see ParamTypeEnum
     */
    @Schema(description = "参数类型")
    private String paramType;

    @Schema(description = "参数值")
    private Object paramValue;

    @Schema(description = "参数名称是否需要转换成下划线命名")
    private boolean underLine = true;

    @Schema(description = "嵌套查询")
    private List<QueryParam> nestedParams;

    public boolean isOr() {
        return or;
    }

    public void setOr(boolean or) {
        this.or = or;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getCompareType() {
        return compareType;
    }

    public void setCompareType(String compareType) {
        this.compareType = compareType;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public Object getParamValue() {
        return paramValue;
    }

    public void setParamValue(Object paramValue) {
        this.paramValue = paramValue;
    }

    public boolean isUnderLine() {
        return underLine;
    }

    public void setUnderLine(boolean underLine) {
        this.underLine = underLine;
    }

    public List<QueryParam> getNestedParams() {
        return nestedParams;
    }

    public void setNestedParams(List<QueryParam> nestedParams) {
        this.nestedParams = nestedParams;
    }
}
