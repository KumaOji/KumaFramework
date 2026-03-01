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

package com.kuma.boot.web.support.enums.mvc.converter;

import com.kuma.boot.common.enums.base.CommonEnum;
import com.kuma.boot.web.support.enums.mvc.CommonEnumRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Order(1)
@Component
public class CommonEnumConverter implements ConditionalGenericConverter {

    @Autowired private CommonEnumRegistry enumRegistry;

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        Class<?> type = targetType.getType();
        return enumRegistry.getClassDict().containsKey(type);
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return enumRegistry.getClassDict().keySet().stream()
                .map(cls -> new ConvertiblePair(String.class, cls))
                .collect(Collectors.toSet());
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        String value = (String) source;
        List<CommonEnum> commonEnums = this.enumRegistry.getClassDict().get(targetType.getType());
        return commonEnums.stream()
                .filter(commonEnum -> commonEnum.match(value))
                .findFirst()
                .orElse(null);
    }
}
