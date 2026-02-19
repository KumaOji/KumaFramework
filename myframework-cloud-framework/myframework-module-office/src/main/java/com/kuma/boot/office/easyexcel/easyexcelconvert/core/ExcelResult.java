/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.office.easyexcel.easyexcelconvert.core;

import java.util.List;

public interface ExcelResult<T> {
    public List<T> getList();

    public List<String> getErrorList();

    public String getAnalysis();
}

