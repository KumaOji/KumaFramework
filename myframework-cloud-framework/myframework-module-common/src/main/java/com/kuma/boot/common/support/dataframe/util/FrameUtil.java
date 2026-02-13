/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.util;

import com.kuma.boot.common.support.dataframe.iframe.item.FI2;
import com.kuma.boot.common.support.dataframe.iframe.item.FI3;
import com.kuma.boot.common.support.dataframe.iframe.item.FI4;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FrameUtil {
    private FrameUtil() {
    }

    public static <K, V> List<FI2<K, V>> toListFI2(Map<K, V> resultMap) {
        return resultMap.entrySet().stream().map(e -> new FI2(e.getKey(), e.getValue())).collect(Collectors.toList());
    }

    public static <K, J, V> List<FI3<K, J, V>> toListFI3(Map<K, Map<J, V>> map) {
        return map.entrySet().stream().flatMap(et -> ((Map)et.getValue()).entrySet().stream().map(subEt -> new FI3(et.getKey(), subEt.getKey(), subEt.getValue())).collect(Collectors.toList()).stream()).collect(Collectors.toList());
    }

    public static <K, J, H, V> List<FI4<K, J, H, V>> toListFI4(Map<K, Map<J, Map<H, V>>> map) {
        return map.entrySet().stream().flatMap(et -> ((Map)et.getValue()).entrySet().stream().flatMap(subEt -> ((Map)subEt.getValue()).entrySet().stream().map(sub2Et -> new FI4(et.getKey(), subEt.getKey(), sub2Et.getKey(), sub2Et.getValue())).collect(Collectors.toList()).stream()).collect(Collectors.toList()).stream()).collect(Collectors.toList());
    }
}

