/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.collection.CollUtil
 *  cn.hutool.core.util.StrUtil
 *  com.alibaba.excel.context.AnalysisContext
 *  com.alibaba.excel.exception.ExcelDataConvertException
 *  com.alibaba.excel.metadata.data.ReadCellData
 *  com.alibaba.excel.read.listener.ReadListener
 *  com.alibaba.excel.util.ConverterUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.apache.commons.compress.utils.Lists
 */
package com.kuma.boot.office.easyexcel.easyexcelimport.excel;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ConverterUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.office.easyexcel.easyexcelimport.constant.ImportConstant;
import com.kuma.boot.office.easyexcel.easyexcelimport.refactor.ThrowingConsumer;
import com.kuma.boot.office.easyexcel.easyexcelimport.valid.ImportValid;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.compress.utils.Lists;

public class ExcelImportCommonListener<T>
implements ReadListener<T> {
    private List<T> persistentDataList = Lists.newArrayList();
    private final ThrowingConsumer<List<T>> persistentActionMethod;
    private List<String> errorLogList = new ArrayList<String>();
    private Long count = 1000L;

    public ExcelImportCommonListener(ThrowingConsumer<List<T>> persistentActionMethod) {
        this.persistentActionMethod = persistentActionMethod;
    }

    public ExcelImportCommonListener(ThrowingConsumer<List<T>> persistentActionMethod, List<String> errorLogLIst) {
        this.persistentActionMethod = persistentActionMethod;
        this.errorLogList = errorLogLIst;
    }

    public void onException(Exception exception, AnalysisContext context) throws Exception {
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException)exception;
            LogUtils.error((String)"\u7b2c{}\u884c\uff0c\u7b2c{}\u5217\u89e3\u6790\u5f02\u5e38\uff0c\u6570\u636e\u4e3a:{}", (Object[])new Object[]{excelDataConvertException.getRowIndex(), excelDataConvertException.getColumnIndex(), excelDataConvertException.getCellData().getStringValue()});
            if (Objects.nonNull(this.errorLogList)) {
                String errorLog = "\u7b2c" + excelDataConvertException.getRowIndex() + "\u884c\uff0c\u7b2c" + excelDataConvertException.getColumnIndex() + "\u5217\u89e3\u6790\u5f02\u5e38\uff0c\u6570\u636e\u4e3a:" + excelDataConvertException.getCellData().getStringValue();
                this.errorLogList.add(errorLog);
            }
        }
    }

    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        Map headMapping = ConverterUtils.convertToStringMap(headMap, (AnalysisContext)context);
        LogUtils.info((String)("\u8868\u5934\u6570\u636e: " + StrUtil.toString((Object)headMapping)), (Object[])new Object[0]);
        if (CollUtil.isEmpty((Map)headMapping)) {
            this.errorLogList.add("The header of file can't be empty!");
        }
    }

    public void invoke(T t, AnalysisContext analysisContext) {
        try {
            Class<?> aClass = t.getClass();
            Field relationIdField = aClass.getDeclaredField("relationId");
            relationIdField.setAccessible(Boolean.TRUE);
            Long l = this.count;
            this.count = this.count + 1L;
            relationIdField.set(t, l);
        }
        catch (Exception e) {
            LogUtils.error((String)"in error{}", (Object[])new Object[]{e});
            this.errorLogList.add("The Row Data inject relationId field in error");
        }
        ImportValid.validRequireField(t, this.errorLogList);
        if (Objects.isNull(this.errorLogList) || CollUtil.isEmpty(this.errorLogList)) {
            this.persistentDataList.add(t);
            if (this.persistentDataList.size() >= ImportConstant.MAX_INSERT_COUNT) {
                this.persistentDataToDb(this.persistentDataList);
                this.persistentDataList.clear();
            }
        }
    }

    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        if (CollUtil.isNotEmpty(this.persistentDataList)) {
            this.persistentDataToDb(this.persistentDataList);
        }
    }

    private void persistentDataToDb(List<T> data) {
        Collection dataList = null;
        dataList.stream().forEach(this.persistentActionMethod);
    }
}

