/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.enums.base.CommonEnum
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.core.annotation.Order
 *  org.springframework.core.convert.TypeDescriptor
 *  org.springframework.core.convert.converter.ConditionalGenericConverter
 *  org.springframework.core.convert.converter.GenericConverter$ConvertiblePair
 *  org.springframework.stereotype.Component
 */
package com.kuma.boot.web.support.enums.mvc.converter;

import com.kuma.boot.common.enums.base.CommonEnum;
import com.kuma.boot.web.support.enums.mvc.CommonEnumRegistry;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.stereotype.Component;

@Order(value=1)
@Component
public class CommonEnumConverter
implements ConditionalGenericConverter {
    @Autowired
    private CommonEnumRegistry enumRegistry;

    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        Class type = targetType.getType();
        return this.enumRegistry.getClassDict().containsKey(type);
    }

    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return this.enumRegistry.getClassDict().keySet().stream().map(cls -> new GenericConverter.ConvertiblePair(String.class, cls)).collect(Collectors.toSet());
    }

    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        String value = (String)source;
        List<CommonEnum> commonEnums = this.enumRegistry.getClassDict().get(targetType.getType());
        return commonEnums.stream().filter(commonEnum -> commonEnum.match(value)).findFirst().orElse(null);
    }
}

