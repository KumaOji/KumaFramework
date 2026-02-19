/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.idev.excel.context.AnalysisContext
 */
package com.kuma.boot.office.fastexcel.listener;

import cn.idev.excel.context.AnalysisContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class TypeExcelMapReadListener<T>
implements ExcelMapReadListener<T> {
    private final Map<String, Collection<T>> mapData = new HashMap<String, Collection<T>>();

    @Override
    public Map<String, ? extends Collection<T>> getMapData() {
        return this.mapData;
    }

    public void invoke(T data, AnalysisContext context) {
        String sheetName = context.readSheetHolder().getSheetName();
        Collection infos = this.mapData.computeIfAbsent(sheetName, s -> new ArrayList());
        infos.add(data);
    }
}

