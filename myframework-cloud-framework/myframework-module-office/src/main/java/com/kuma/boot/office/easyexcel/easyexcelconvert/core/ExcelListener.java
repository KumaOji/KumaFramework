/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.alibaba.excel.read.listener.ReadListener
 */
package com.kuma.boot.office.easyexcel.easyexcelconvert.core;

import com.alibaba.excel.read.listener.ReadListener;

public interface ExcelListener<T>
extends ReadListener<T> {
    public ExcelResult<T> getExcelResult();
}

