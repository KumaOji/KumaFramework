/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.text.CharSequenceUtil
 *  cn.hutool.core.util.ClassUtil
 *  com.kuma.boot.common.enums.base.CommonEnum
 *  io.swagger.v3.core.converter.AnnotatedType
 *  io.swagger.v3.oas.models.media.Schema
 *  io.swagger.v3.oas.models.parameters.Parameter
 *  org.springdoc.core.customizers.ParameterCustomizer
 *  org.springdoc.core.customizers.PropertyCustomizer
 *  org.springframework.core.MethodParameter
 *  tools.jackson.databind.type.CollectionType
 *  tools.jackson.databind.type.SimpleType
 */
package com.kuma.boot.springdoc.springdocextension.handler;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ClassUtil;
import com.kuma.boot.common.enums.base.CommonEnum;
import com.kuma.boot.springdoc.springdocextension.util.DocUtils;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import org.springdoc.core.customizers.ParameterCustomizer;
import org.springdoc.core.customizers.PropertyCustomizer;
import org.springframework.core.MethodParameter;
import tools.jackson.databind.type.CollectionType;
import tools.jackson.databind.type.SimpleType;

public class BaseEnumParameterHandler
implements ParameterCustomizer,
PropertyCustomizer {
    public Parameter customize(Parameter parameterModel, MethodParameter methodParameter) {
        Class parameterType = methodParameter.getParameterType();
        if (!ClassUtil.isAssignable(CommonEnum.class, (Class)parameterType)) {
            return parameterModel;
        }
        String description = parameterModel.getDescription();
        if (CharSequenceUtil.contains((CharSequence)description, (CharSequence)"color:red")) {
            return parameterModel;
        }
        this.configureSchema(parameterModel.getSchema(), parameterType);
        parameterModel.setDescription(this.appendEnumDescription(description, parameterType));
        return parameterModel;
    }

    public Schema customize(Schema schema, AnnotatedType type) {
        Class<?> rawClass = this.resolveRawClass(type.getType());
        if (!ClassUtil.isAssignable(CommonEnum.class, rawClass)) {
            return schema;
        }
        this.configureSchema(schema, rawClass);
        schema.setDescription(this.appendEnumDescription(schema.getDescription(), rawClass));
        return schema;
    }

    private void configureSchema(Schema schema, Class<?> enumClass) {
        CommonEnum[] enums = (CommonEnum[])enumClass.getEnumConstants();
        List<String> valueList = Arrays.stream(enums).map(e -> e.getName().toString()).toList();
        schema.setEnum(valueList);
        String enumValueType = DocUtils.getEnumValueTypeAsString(enumClass);
        schema.setType(enumValueType);
        schema.setFormat(DocUtils.resolveFormat(enumValueType));
    }

    private String appendEnumDescription(String originalDescription, Class<?> enumClass) {
        return originalDescription + "<span style='color:red'>" + String.valueOf(DocUtils.getDescMap(enumClass)) + "</span>";
    }

    private Class<?> resolveRawClass(Type type) {
        if (type instanceof SimpleType) {
            SimpleType simpleType = (SimpleType)type;
            return simpleType.getRawClass();
        }
        if (type instanceof CollectionType) {
            CollectionType collectionType = (CollectionType)type;
            return collectionType.getContentType().getRawClass();
        }
        return Object.class;
    }
}

