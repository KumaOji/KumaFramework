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

package com.kuma.boot.data.mybatis.mybatisplus.base.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.validation.groups.Default;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * SuperEntity
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-04 07:40:46
 */
public class MpSuperEntity<I extends Serializable> implements Serializable {

    @Serial private static final long serialVersionUID = -4603650115461757622L;

    public static final String ID = "id";
    public static final String CREATE_DATE = "createDate";
    public static final String CREATED_USER = "createUser";
    public static final String MODIFY_DATE = "modifyDate";
    public static final String MODIFY_USER = "modifyUser";

    @TableId(value = "id", type = IdType.AUTO)
    protected Long id;

    @TableField(value = "create_date", fill = FieldFill.INSERT)
    private LocalDateTime createDate;

    @TableField(value = "created_user", fill = FieldFill.INSERT)
    private String createUser;

    @TableField(value = "modify_date", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime modifyDate;

    @TableField(value = "modify_user", fill = FieldFill.INSERT_UPDATE)
    private String modifyUser;

    /** 保存和缺省验证组 */
    public interface Save extends Default {}

    /** 更新和缺省验证组 */
    public interface Update extends Default {}

    public MpSuperEntity() {}

    public MpSuperEntity(Long id, LocalDateTime createDate, String createUser, LocalDateTime modifyDate,
                         String modifyUser) {
        this.id = id;
        this.createDate = createDate;
        this.createUser = createUser;
        this.modifyDate = modifyDate;
        this.modifyUser = modifyUser;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public LocalDateTime getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(LocalDateTime modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }
}
