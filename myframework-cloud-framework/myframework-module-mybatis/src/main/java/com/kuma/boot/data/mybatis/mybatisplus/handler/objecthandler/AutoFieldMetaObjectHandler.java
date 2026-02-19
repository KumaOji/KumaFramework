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

package com.kuma.boot.data.mybatis.mybatisplus.handler.objecthandler;

import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.kuma.boot.common.constant.StrPoolConstants;
import com.kuma.boot.common.holder.UserContextHolder;
import com.kuma.boot.common.utils.id.IdGeneratorUtils;
import com.kuma.boot.data.mybatis.mybatisplus.base.entity.MpSuperEntity;
import com.kuma.boot.data.mybatis.autoconfigure.properties.MybatisPlusAutoFillProperties;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;

import cn.hutool.core.util.ObjUtil;

/**
 * 自定义填充公共字段
 *
 * @author kuma
 * @version 2022.03
 * @since 2020/5/2 11:22
 */
public class AutoFieldMetaObjectHandler implements MetaObjectHandler {

    private final MybatisPlusAutoFillProperties autoFillProperties;

    public AutoFieldMetaObjectHandler(MybatisPlusAutoFillProperties autoFillProperties) {
        this.autoFillProperties = autoFillProperties;
    }

    /** 是否开启了插入填充 */
    @Override
    public boolean openInsertFill(MappedStatement mappedStatement) {
        return autoFillProperties.getEnableInsertFill();
    }

    /** 是否开启了更新填充 */
    @Override
    public boolean openUpdateFill(MappedStatement mappedStatement) {
        return autoFillProperties.getEnableUpdateFill();
    }

    /** 插入填充，字段为空自动填充 */
    @Override
    public void insertFill(MetaObject metaObject) {
        fillId(metaObject);

        Object createTime = getFieldValByName(autoFillProperties.getCreateDateField(), metaObject);
        Object updateTime = getFieldValByName(autoFillProperties.getModifyDateField(), metaObject);

        if (createTime == null || updateTime == null) {
            if (createTime == null) {
                fillCreated(metaObject);
            }
            if (updateTime == null) {
                fillUpdated(metaObject);
            }
        }

    }

    private void fillId(MetaObject metaObject) {
        String id = IdGeneratorUtils.getId();

        // 1. 继承了SuperEntity 若 ID 中有值，就不设置
        if (metaObject.getOriginalObject() instanceof MpSuperEntity) {
            Object oldId = ((MpSuperEntity<?>) metaObject.getOriginalObject()).getId();
            if (oldId != null) {
                return;
            }

            Object idVal =
                    StrPoolConstants.STRING_TYPE_NAME.equals(
                            metaObject
                                    .getGetterType(autoFillProperties.getIdField())
                                    .getName())
                            ? String.valueOf(id)
                            : id;
            this.setFieldValByName(autoFillProperties.getIdField(), idVal, metaObject);
            return;
        }

        // 2. 没有继承SuperEntity， 但主键的字段名为：id
        if (metaObject.hasGetter(autoFillProperties.getIdField())) {
            Object oldId = metaObject.getValue(autoFillProperties.getIdField());
            if (oldId != null) {
                return;
            }

            Object idVal =
                    StrPoolConstants.STRING_TYPE_NAME.equals(
                            metaObject
                                    .getGetterType(autoFillProperties.getIdField())
                                    .getName())
                            ? String.valueOf(id)
                            : id;
            this.setFieldValByName(autoFillProperties.getIdField(), idVal, metaObject);
            return;
        }

        // 3. 实体没有继承 Entity 和 SuperEntity，且 主键名为其他字段
        TableInfo tableInfo =
                TableInfoHelper.getTableInfo(metaObject.getOriginalObject().getClass());
        if (tableInfo == null) {
            return;
        }

        // 主键类型
        Class<?> keyType = tableInfo.getKeyType();
        if (keyType == null) {
            return;
        }

        // id 字段名
        String keyProperty = tableInfo.getKeyProperty();
        Object oldId = metaObject.getValue(keyProperty);
        if (oldId != null) {
            return;
        }

        // 反射得到 主键的值
        Field idField = ReflectUtil.getField(metaObject.getOriginalObject().getClass(), keyProperty);
        Object fieldValue = ReflectUtil.getFieldValue(metaObject.getOriginalObject(), idField);
        // 判断ID 是否有值，有值就不
        if (ObjUtil.isNotEmpty(fieldValue)) {
            return;
        }

        Object idVal =
                keyType.getName().equalsIgnoreCase(StrPoolConstants.STRING_TYPE_NAME)
                        ? String.valueOf(id)
                        : id;
        this.setFieldValByName(keyProperty, idVal, metaObject);
    }

    private void fillCreated(MetaObject metaObject) {
        // 设置创建时间和创建人
        if (metaObject.getOriginalObject() instanceof MpSuperEntity) {
            created(metaObject);
            return;
        }

        if (metaObject.hasGetter(autoFillProperties.getCreateUserField())) {
            Object oldVal = metaObject.getValue(autoFillProperties.getCreateUserField());
            if (oldVal == null) {
                this.setFieldValByName(
                        autoFillProperties.getCreateUserField(),
                        UserContextHolder.getUserId(),
                        metaObject);
            }
        }

        if (metaObject.hasGetter(autoFillProperties.getCreateDateField())) {
            Object oldVal = metaObject.getValue(autoFillProperties.getCreateDateField());
            if (oldVal == null) {
                this.setFieldValByName(
                        autoFillProperties.getCreateDateField(), LocalDateTime.now(), metaObject);
            }
        }
    }

    private void created(MetaObject metaObject) {
        MpSuperEntity entity = (MpSuperEntity) metaObject.getOriginalObject();
        if (entity.getCreateDate() == null) {
            this.setFieldValByName(
                    autoFillProperties.getCreateDateField(), LocalDateTime.now(), metaObject);
        }

        if (entity.getCreateUser() == null || entity.getCreateUser().equals("system")) {
            Object userIdVal = UserContextHolder.getUserId();
            this.setFieldValByName(MpSuperEntity.CREATED_USER, userIdVal, metaObject);
        }
    }

    private void fillUpdated(MetaObject metaObject) {
        // 修改人 修改时间
        if (metaObject.getOriginalObject() instanceof MpSuperEntity) {
            update(metaObject);
            return;
        }

        if (metaObject.hasGetter(autoFillProperties.getModifyUserField())) {
            Object oldVal = metaObject.getValue(autoFillProperties.getModifyUserField());
            if (oldVal == null) {
                this.setFieldValByName(
                        autoFillProperties.getModifyUserField(),
                        UserContextHolder.getUserId(),
                        metaObject);
            }
        }

        if (metaObject.hasGetter(autoFillProperties.getModifyDateField())) {
            Object oldVal = metaObject.getValue(autoFillProperties.getModifyDateField());
            if (oldVal == null) {
                this.setFieldValByName(
                        autoFillProperties.getModifyDateField(), LocalDateTime.now(), metaObject);
            }
        }
    }

    private void update(MetaObject metaObject) {
        MpSuperEntity entity = (MpSuperEntity) metaObject.getOriginalObject();
        if (entity.getModifyUser() == null || entity.getModifyUser().equals("system")) {
            Object userIdVal = UserContextHolder.getUserId();
            this.setFieldValByName(MpSuperEntity.MODIFY_USER, userIdVal, metaObject);
        }

        if (entity.getModifyDate() == null) {
            this.setFieldValByName(
                    autoFillProperties.getModifyDateField(), LocalDateTime.now(), metaObject);
        }
    }

    /** 更新填充 */
    @Override
    public void updateFill(MetaObject metaObject) {
        fillUpdated(metaObject);
    }
}
