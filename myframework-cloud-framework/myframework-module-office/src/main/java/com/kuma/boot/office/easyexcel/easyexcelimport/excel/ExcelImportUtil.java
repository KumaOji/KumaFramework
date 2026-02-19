/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.alibaba.excel.EasyExcel
 */
package com.kuma.boot.office.easyexcel.easyexcelimport.excel;

import com.alibaba.excel.EasyExcel;
import com.kuma.boot.office.easyexcel.easyexcelimport.refactor.ThrowingConsumer;
import java.io.InputStream;
import java.util.List;

public class ExcelImportUtil<T> {
    public static <T> void importFile(InputStream fileStream, T rowDto, ThrowingConsumer<List<T>> rowAction) {
        ExcelImportCommonListener<T> commonListener = new ExcelImportCommonListener<T>(rowAction);
        EasyExcel.read((InputStream)fileStream, rowDto.getClass(), commonListener).sheet().doRead();
    }
}

