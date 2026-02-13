/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.collection.CollUtil
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.core.convert.support.DefaultConversionService
 */
package com.kuma.boot.common.model;

import cn.hutool.core.collection.CollUtil;
import java.util.List;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;

public class EnhancedConversionService
extends DefaultConversionService {
    public EnhancedConversionService(List<Converter> converters) {
        if (CollUtil.isNotEmpty(converters)) {
            for (Converter converter : converters) {
                this.addConverter(converter);
            }
        }
    }
}

