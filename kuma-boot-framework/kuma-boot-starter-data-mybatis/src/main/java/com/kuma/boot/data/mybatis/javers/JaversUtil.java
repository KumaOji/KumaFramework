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

package com.kuma.boot.data.mybatis.javers;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.mybatis.javers.annotation.Column;
import com.kuma.boot.data.mybatis.javers.comparator.CustomerLabelComparator;
import com.kuma.boot.data.mybatis.javers.comparator.CustomerTypeComparator;
import com.kuma.boot.data.mybatis.javers.comparator.DealerComparator;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import org.javers.core.Changes;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.NewObject;
import org.javers.core.diff.changetype.ObjectRemoved;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.jspecify.annotations.NonNull;
import org.springframework.util.Assert;

/**
 * JaversUtil
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class JaversUtil<T> {

    private static final Javers JAVERS =
            JaversBuilder.javers()
                    .registerValue(CustomerLabel.class, new CustomerLabelComparator())
                    .registerValue(Dealer.class, new DealerComparator())
                    .registerValue(CustomerTypeEnum.class, new CustomerTypeComparator())
                    .build();

    public static <T> Changes diff( @NonNull T before, @NonNull T after ) {
        Diff compare = JAVERS.compare(before, after);
        Changes changes = compare.getChanges();
        if (CollUtil.isNotEmpty(changes)) {
            for (Change change : changes) {
                Optional<Object> affectedObject = change.getAffectedObject();
                if (affectedObject.isPresent()) {
                    if (change instanceof NewObject newObj) {
                        // 新增
                        LogUtils.info("新增=>" + newObj);
                    } else if (change instanceof ObjectRemoved objRemoved) {
                        // 元素删除
                        LogUtils.info("删除=>" + objRemoved);
                    } else if (change instanceof ValueChange valueChange) {
                        // 值变化
                        String propertyName = valueChange.getPropertyName();
                        Field field =
                                getField(propertyName, change.getAffectedObject().get().getClass());
                        Assert.notNull(field, "");
                        Column annotation = field.getAnnotation(Column.class);
                        LogUtils.info("type: {}", field.getType());
                        field.setAccessible(true);

                        LogUtils.info(
                                annotation.name()
                                        + ": "
                                        + str(valueChange.getLeft())
                                        + "修改=> "
                                        + str(valueChange.getRight()));
                    } else if (change instanceof ListChange listChange) {
                        // 集合变化
                        LogUtils.info("left: {}", listChange.getLeft());
                        LogUtils.info("right: {}", listChange.getRight());
                        LogUtils.info("集合变化=>" + listChange);
                    }
                }
            }
        }
        return null;
    }

    private static String str( Object obj ) {
        if (obj instanceof CustomerTypeEnum) {
            return ( (CustomerTypeEnum) obj ).getMsg();
        } else if (obj instanceof Dealer) {
            return ( (Dealer) obj ).getDearName();
        }
        return null;
    }

    private static List<Field> getFields( Class clazz ) {
        List<Field> annotationFieldList =
                Arrays.stream(clazz.getDeclaredFields())
                        .filter(field -> field.isAnnotationPresent(Column.class))
                        .collect(Collectors.toList());
        if (CollUtil.isEmpty(annotationFieldList)) {
            return Collections.emptyList();
        }
        return annotationFieldList;
    }

    private static Field getField( String fieldName, Class clazz ) {
        List<Field> fields = getFields(clazz);
        if (CollUtil.isEmpty(fields)) {
            return null;
        }
        return fields.stream()
                .filter(field -> fieldName.equals(field.getName()))
                .findFirst()
                .orElse(null);
    }

    public static <T> Changes diff( List<T> before, List<T> after, Class<T> clazz ) {
        Diff diff = JAVERS.compareCollections(before, after, clazz);
        return diff.getChanges();
    }
}
