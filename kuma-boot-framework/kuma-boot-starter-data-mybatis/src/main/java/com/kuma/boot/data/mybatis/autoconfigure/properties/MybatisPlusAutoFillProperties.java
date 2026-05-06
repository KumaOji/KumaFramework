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

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * MybatisPlusAutoFillProperties
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-04 07:44:25
 */
@ConfigurationProperties(prefix = MybatisPlusAutoFillProperties.PREFIX)
public class MybatisPlusAutoFillProperties {

    public static final String PREFIX = "kuma.boot.data.mybatis.mybatis-plus.auto-fill";

    /** 鏄惁寮€鍚嚜鍔ㄥ～鍏呭瓧娈?*/
    private Boolean enabled = true;

    /** 鏄惁寮€鍚簡鎻掑叆濉厖 */
    private Boolean enableInsertFill = true;

    /** 鏄惁寮€鍚簡鏇存柊濉厖 */
    private Boolean enableUpdateFill = true;

    /** 鍒涘缓鏃堕棿瀛楁鍚?*/
    private String idField = "id";

    /** 鍒涘缓鏃堕棿瀛楁鍚?*/
    private String createDateField = "createDate";

    /** 鏇存柊鏃堕棿瀛楁鍚?*/
    private String modifyDateField = "modifyDate";

    /** 鍒涘缓鏃堕棿瀛楁鍚?*/
    private String createUserField = "createUser";

    /** 鏇存柊鏃堕棿瀛楁鍚?*/
    private String modifyUserField = "modifyUser";

    public String getIdField() {
        return idField;
    }

    public void setIdField(String idField) {
        this.idField = idField;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getEnableInsertFill() {
        return enableInsertFill;
    }

    public void setEnableInsertFill(Boolean enableInsertFill) {
        this.enableInsertFill = enableInsertFill;
    }

    public Boolean getEnableUpdateFill() {
        return enableUpdateFill;
    }

    public void setEnableUpdateFill(Boolean enableUpdateFill) {
        this.enableUpdateFill = enableUpdateFill;
    }

    public String getCreateDateField() {
        return createDateField;
    }

    public void setCreateDateField(String createDateField) {
        this.createDateField = createDateField;
    }

    public String getModifyDateField() {
        return modifyDateField;
    }

    public void setModifyDateField(String modifyDateField) {
        this.modifyDateField = modifyDateField;
    }

    public String getCreateUserField() {
        return createUserField;
    }

    public void setCreateUserField(String createUserField) {
        this.createUserField = createUserField;
    }

    public String getModifyUserField() {
        return modifyUserField;
    }

    public void setModifyUserField(String modifyUserField) {
        this.modifyUserField = modifyUserField;
    }
}
