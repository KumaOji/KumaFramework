/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.map.MapUtil
 *  cn.hutool.core.util.ObjUtil
 *  com.kuma.boot.common.enums.base.CommonEnum
 *  org.springframework.core.convert.converter.Converter
 */
package com.kuma.boot.web.mvc.converter;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjUtil;
import com.kuma.boot.common.enums.base.CommonEnum;
import java.util.Map;
import org.springframework.core.convert.converter.Converter;

public class IntegerToEnumConverter<T extends CommonEnum>
implements Converter<Integer, T> {
    private final Map<Integer, T> enumMap = MapUtil.newHashMap();

    public IntegerToEnumConverter(Class<T> enumType) {
        CommonEnum[] enums;
        for (CommonEnum e : enums = (CommonEnum[])enumType.getEnumConstants()) {
            this.enumMap.put(e.getCode(), e);
        }
    }

    public T convert(Integer source) {
        CommonEnum t = (CommonEnum)this.enumMap.get(source);
        if (ObjUtil.isNull((Object)t)) {
            throw new IllegalArgumentException("\u65e0\u6cd5\u5339\u914d\u5bf9\u5e94\u7684\u7c7b\u578b");
        }
        return (T)t;
    }
}

