/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 *  org.springframework.core.convert.converter.GenericConverter
 *  org.springframework.core.convert.support.GenericConversionService
 *  org.springframework.format.support.DefaultFormattingConversionService
 */
package com.kuma.boot.common.utils.convert;

import com.kuma.boot.common.utils.convert.EnumToStringConverter;
import com.kuma.boot.common.utils.convert.StringToEnumConverter;
import org.jspecify.annotations.Nullable;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;

public class ConversionService
extends DefaultFormattingConversionService {
    private static volatile @Nullable ConversionService SHARED_INSTANCE;

    public ConversionService() {
        super.addConverter((GenericConverter)new EnumToStringConverter());
        super.addConverter((GenericConverter)new StringToEnumConverter());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static GenericConversionService getInstance() {
        ConversionService sharedInstance = SHARED_INSTANCE;
        if (sharedInstance != null) return sharedInstance;
        Class<ConversionService> clazz = ConversionService.class;
        synchronized (ConversionService.class) {
            sharedInstance = SHARED_INSTANCE;
            if (sharedInstance != null) return sharedInstance;
            SHARED_INSTANCE = sharedInstance = new ConversionService();
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return sharedInstance;
        }
    }
}

