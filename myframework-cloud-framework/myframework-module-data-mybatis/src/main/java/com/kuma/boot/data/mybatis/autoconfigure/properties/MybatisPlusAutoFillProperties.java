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
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * MybatisPlusAutoFillProperties
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-04 07:44:25
 */
@RefreshScope
@ConfigurationProperties(prefix = MybatisPlusAutoFillProperties.PREFIX)
public class MybatisPlusAutoFillProperties {

    public static final String PREFIX = "kuma.boot.data.mybatis.mybatis-plus.auto-fill";

    /** 是否开启自动填充字段 */
    private Boolean enabled = true;

    /** 是否开启了插入填充 */
    private Boolean enableInsertFill = true;

    /** 是否开启了更新填充 */
    private Boolean enableUpdateFill = true;

    /** 创建时间字段名 */
    private String idField = "id";

    /** 创建时间字段名 */
    private String createDateField = "createDate";

    /** 更新时间字段名 */
    private String modifyDateField = "modifyDate";

    /** 创建时间字段名 */
    private String createUserField = "createUser";

    /** 更新时间字段名 */
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
