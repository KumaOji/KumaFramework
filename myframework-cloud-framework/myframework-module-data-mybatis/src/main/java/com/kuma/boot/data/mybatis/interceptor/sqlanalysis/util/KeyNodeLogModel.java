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

package com.kuma.boot.data.mybatis.interceptor.sqlanalysis.util;

import java.util.Date;

/**
 * @Author huhaitao21
 * @Description 关键节点日志模型
 * @since 16:45 2021/4/9
 **/
public class KeyNodeLogModel {

    public KeyNodeLogModel() {
    }

    /**
     * 业务id
     */
    private String businessId;

    /**
     * 业务模块名称
     */
    private String moduleName;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 业务时间
     */
    private Date businessTime;

    /**
     * 日志时间
     */
    private Date logTime;

    /**
     * 描述
     */
    private String describe;

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId( String businessId ) {
        this.businessId = businessId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName( String moduleName ) {
        this.moduleName = moduleName;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName( String nodeName ) {
        this.nodeName = nodeName;
    }

    public Date getBusinessTime() {
        return businessTime;
    }

    public void setBusinessTime( Date businessTime ) {
        this.businessTime = businessTime;
    }

    public Date getLogTime() {
        return logTime;
    }

    public void setLogTime( Date logTime ) {
        this.logTime = logTime;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe( String describe ) {
        this.describe = describe;
    }

    public KeyNodeLogModel(
            String businessId,
            String moduleName,
            String nodeName,
            Date businessTime,
            Date logTime,
            String describe ) {
        this.businessId = businessId;
        this.moduleName = moduleName;
        this.nodeName = nodeName;
        this.businessTime = businessTime;
        this.logTime = logTime;
        this.describe = describe;
    }

    public static KeyNodeLogModelBuilder builder() {
        return new KeyNodeLogModelBuilder();
    }

    /**
     * KeyNodeLogModelBuilder
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    public static final class KeyNodeLogModelBuilder {

        private String businessId;
        private String moduleName;
        private String nodeName;
        private Date businessTime;
        private Date logTime;
        private String describe;

        private KeyNodeLogModelBuilder() {
        }

        public KeyNodeLogModelBuilder businessId( String businessId ) {
            this.businessId = businessId;
            return this;
        }

        public KeyNodeLogModelBuilder moduleName( String moduleName ) {
            this.moduleName = moduleName;
            return this;
        }

        public KeyNodeLogModelBuilder nodeName( String nodeName ) {
            this.nodeName = nodeName;
            return this;
        }

        public KeyNodeLogModelBuilder businessTime( Date businessTime ) {
            this.businessTime = businessTime;
            return this;
        }

        public KeyNodeLogModelBuilder logTime( Date logTime ) {
            this.logTime = logTime;
            return this;
        }

        public KeyNodeLogModelBuilder describe( String describe ) {
            this.describe = describe;
            return this;
        }

        public KeyNodeLogModel build() {
            KeyNodeLogModel keyNodeLogModel = new KeyNodeLogModel();
            keyNodeLogModel.setBusinessId(businessId);
            keyNodeLogModel.setModuleName(moduleName);
            keyNodeLogModel.setNodeName(nodeName);
            keyNodeLogModel.setBusinessTime(businessTime);
            keyNodeLogModel.setLogTime(logTime);
            keyNodeLogModel.setDescribe(describe);
            return keyNodeLogModel;
        }
    }
}
