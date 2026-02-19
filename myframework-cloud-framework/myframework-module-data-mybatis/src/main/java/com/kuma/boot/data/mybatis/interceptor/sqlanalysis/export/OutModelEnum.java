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

package com.kuma.boot.data.mybatis.interceptor.sqlanalysis.export;

/**
 * @Author huhaitao21
 * @Description 分析结果输出模式
 * @since 14:15 2023/2/8
 **/
public enum OutModelEnum {
    LOG("LOG", "日志方式输出"),
    MQ("MQ", "发送mq"),
    MYSQL("MYSQL", "mysql表存储");

    OutModelEnum(String modelType, String modelDesc) {
        this.modelType = modelType;
        this.modelDesc = modelDesc;
    }

    /**
     * 模式类型
     */
    private String modelType;

    /**
     * 模式描述
     */
    private String modelDesc;

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    public String getModelDesc() {
        return modelDesc;
    }

    public void setModelDesc(String modelDesc) {
        this.modelDesc = modelDesc;
    }
}
