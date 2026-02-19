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

package com.kuma.boot.data.mybatis.delay;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * AppDelayMessage
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableName(value = "app_delay_message")
public class AppDelayMessage {

    /**
     * ID
     */
    private String id;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 超时时长(H)
     */
    private Integer ttl;

    /**
     * type1, type2
     */
    private Type type;

    /**
     * 编译:COMPILE; 测试:TEST;
     */
    private Stage stage;

    /**
     * 待处理:PENDING; 已处理:PROCESSED; 超时:TIMEOUT; 无效:INVALID;
     */
    private Status status;

    /**
     * 备考
     */
    private String remark;

    /**
     * 回调函数
     */
    private String callback;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime modifyTime;

    /**
     * 删除标
     */
    private String deleteFlg;

    public AppDelayMessage() {
    }

    public AppDelayMessage(
            String id,
            String appId,
            Integer ttl,
            Type type,
            Stage stage,
            Status status,
            String remark,
            String callback,
            LocalDateTime createTime,
            LocalDateTime modifyTime,
            String deleteFlg ) {
        this.id = id;
        this.appId = appId;
        this.ttl = ttl;
        this.type = type;
        this.stage = stage;
        this.status = status;
        this.remark = remark;
        this.callback = callback;
        this.createTime = createTime;
        this.modifyTime = modifyTime;
        this.deleteFlg = deleteFlg;
    }

    /**
     * 场景类型
     */
    public enum Type {
        /**
         * 场景1
         */
        TYPE1,
        /**
         * 场景2
         */
        TYPE2
    }

    /**
     * 阶段
     */
    public enum Stage {
        /**
         * 编译
         */
        REAL_COMPILE
    }

    /**
     * 状态
     */
    public enum Status {
        /**
         * 待处理
         */
        PENDING,
        /**
         * 已处理
         */
        PROCESSED,
        /**
         * 超时
         */
        TIMEOUT,
        /**
         * 无效
         */
        INVALID
    }

    public String getDeleteFlg() {
        return deleteFlg;
    }

    public void setDeleteFlg( String deleteFlg ) {
        this.deleteFlg = deleteFlg;
    }

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId( String appId ) {
        this.appId = appId;
    }

    public Integer getTtl() {
        return ttl;
    }

    public void setTtl( Integer ttl ) {
        this.ttl = ttl;
    }

    public Type getType() {
        return type;
    }

    public void setType( Type type ) {
        this.type = type;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage( Stage stage ) {
        this.stage = stage;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus( Status status ) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark( String remark ) {
        this.remark = remark;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback( String callback ) {
        this.callback = callback;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime( LocalDateTime createTime ) {
        this.createTime = createTime;
    }

    public LocalDateTime getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime( LocalDateTime modifyTime ) {
        this.modifyTime = modifyTime;
    }

    @Override
    public String toString() {
        return "AppDelayMessage{"
                + "id='"
                + id
                + '\''
                + ", appId='"
                + appId
                + '\''
                + ", ttl="
                + ttl
                + ", type="
                + type
                + ", stage="
                + stage
                + ", status="
                + status
                + ", remark='"
                + remark
                + '\''
                + ", callback='"
                + callback
                + '\''
                + ", createTime="
                + createTime
                + ", modifyTime="
                + modifyTime
                + ", deleteFlg='"
                + deleteFlg
                + '\''
                + '}';
    }

    public static AppDelayMessageBuilder builder() {
        return new AppDelayMessageBuilder();
    }

    /**
     * AppDelayMessageBuilder
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    public static final class AppDelayMessageBuilder {

        private String id;
        private String appId;
        private Integer ttl;
        private Type type;
        private Stage stage;
        private Status status;
        private String remark;
        private String callback;
        private LocalDateTime createTime;
        private LocalDateTime modifyTime;
        private String deleteFlg;

        private AppDelayMessageBuilder() {
        }

        public AppDelayMessageBuilder id( String id ) {
            this.id = id;
            return this;
        }

        public AppDelayMessageBuilder appId( String appId ) {
            this.appId = appId;
            return this;
        }

        public AppDelayMessageBuilder ttl( Integer ttl ) {
            this.ttl = ttl;
            return this;
        }

        public AppDelayMessageBuilder type( Type type ) {
            this.type = type;
            return this;
        }

        public AppDelayMessageBuilder stage( Stage stage ) {
            this.stage = stage;
            return this;
        }

        public AppDelayMessageBuilder status( Status status ) {
            this.status = status;
            return this;
        }

        public AppDelayMessageBuilder remark( String remark ) {
            this.remark = remark;
            return this;
        }

        public AppDelayMessageBuilder callback( String callback ) {
            this.callback = callback;
            return this;
        }

        public AppDelayMessageBuilder createTime( LocalDateTime createTime ) {
            this.createTime = createTime;
            return this;
        }

        public AppDelayMessageBuilder modifyTime( LocalDateTime modifyTime ) {
            this.modifyTime = modifyTime;
            return this;
        }

        public AppDelayMessageBuilder deleteFlg( String deleteFlg ) {
            this.deleteFlg = deleteFlg;
            return this;
        }

        public AppDelayMessage build() {
            AppDelayMessage appDelayMessage = new AppDelayMessage();
            appDelayMessage.setId(id);
            appDelayMessage.setAppId(appId);
            appDelayMessage.setTtl(ttl);
            appDelayMessage.setType(type);
            appDelayMessage.setStage(stage);
            appDelayMessage.setStatus(status);
            appDelayMessage.setRemark(remark);
            appDelayMessage.setCallback(callback);
            appDelayMessage.setCreateTime(createTime);
            appDelayMessage.setModifyTime(modifyTime);
            appDelayMessage.setDeleteFlg(deleteFlg);
            return appDelayMessage;
        }
    }
}
