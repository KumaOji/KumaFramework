/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.idev.excel.context.AnalysisContext
 *  cn.idev.excel.read.listener.ReadListener
 */
package com.kuma.boot.office.fastexcel.listener;

import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.read.listener.ReadListener;
import com.kuma.boot.office.fastexcel.ExcelDataType;
import java.util.Collection;
import java.util.Map;

public interface ExcelMapReadListener<T>
extends ReadListener<T> {
    default public void doAfterAllAnalysed(AnalysisContext context) {
    }

    default public Collection<T> getCollectionData() {
        return this.getMapData().values().stream().flatMap(Collection::stream).toList();
    }

    public Map<String, ? extends Collection<T>> getMapData();

    default public Object getData(ExcelDataType type) {
        return switch (type) {
            default -> throw new MatchException(null, null);
            case ExcelDataType.MAP -> this.getMapData();
            case ExcelDataType.COLLECTION -> this.getCollectionData();
        };
    }
}

