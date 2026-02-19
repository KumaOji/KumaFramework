/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.alibaba.excel.annotation.ExcelProperty
 *  com.alibaba.excel.context.AnalysisContext
 *  com.alibaba.excel.event.AnalysisEventListener
 *  com.alibaba.excel.exception.ExcelAnalysisException
 *  com.alibaba.excel.util.StringUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 */
package com.kuma.boot.office.easyexcel.easyexcelcheck;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.alibaba.excel.util.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EasyExcelListener<T>
extends AnalysisEventListener<T> {
    private List<T> successList = new ArrayList<T>();
    private List<ExcelCheckErrDto<T>> errList = new ArrayList<ExcelCheckErrDto<T>>();
    private ExcelCheckManager<T> excelCheckManager;
    private List<T> list = new ArrayList<T>();
    private Class<T> clazz;

    public EasyExcelListener(ExcelCheckManager<T> excelCheckManager) {
        this.excelCheckManager = excelCheckManager;
    }

    public EasyExcelListener(ExcelCheckManager<T> excelCheckManager, Class<T> clazz) {
        this.excelCheckManager = excelCheckManager;
        this.clazz = clazz;
    }

    public void invoke(T t, AnalysisContext analysisContext) {
        String errMsg;
        try {
            errMsg = EasyExcelValiHelper.validateEntity(t);
        }
        catch (NoSuchFieldException e) {
            errMsg = "\u89e3\u6790\u6570\u636e\u51fa\u9519";
            LogUtils.error((Throwable)e);
        }
        if (!StringUtils.isEmpty((CharSequence)errMsg)) {
            ExcelCheckErrDto<T> excelCheckErrDto = new ExcelCheckErrDto<T>(t, errMsg);
            this.errList.add(excelCheckErrDto);
        } else {
            this.list.add(t);
        }
        if (this.list.size() > 1000) {
            ExcelCheckResult result = this.excelCheckManager.checkImportExcel(this.list);
            this.successList.addAll(result.getSuccessDtos());
            this.errList.addAll(result.getErrDtos());
            this.list.clear();
        }
    }

    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        ExcelCheckResult result = this.excelCheckManager.checkImportExcel(this.list);
        this.successList.addAll(result.getSuccessDtos());
        this.errList.addAll(result.getErrDtos());
        this.list.clear();
    }

    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        super.invokeHeadMap(headMap, context);
        if (this.clazz != null) {
            try {
                Map<Integer, String> indexNameMap = this.getIndexNameMap(this.clazz);
                Set<Integer> keySet = indexNameMap.keySet();
                for (Integer key : keySet) {
                    if (StringUtils.isEmpty((CharSequence)headMap.get(key))) {
                        throw new ExcelAnalysisException("\u89e3\u6790excel\u51fa\u9519\uff0c\u8bf7\u4f20\u5165\u6b63\u786e\u683c\u5f0f\u7684excel");
                    }
                    if (headMap.get(key).equals(indexNameMap.get(key))) continue;
                    throw new ExcelAnalysisException("\u89e3\u6790excel\u51fa\u9519\uff0c\u8bf7\u4f20\u5165\u6b63\u786e\u683c\u5f0f\u7684excel");
                }
            }
            catch (NoSuchFieldException e) {
                LogUtils.error((Throwable)e);
            }
        }
    }

    public Map<Integer, String> getIndexNameMap(Class clazz) throws NoSuchFieldException {
        HashMap<Integer, String> result = new HashMap<Integer, String>();
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; ++i) {
            Field field = clazz.getDeclaredField(fields[i].getName());
            field.setAccessible(true);
            ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
            if (excelProperty == null) continue;
            int index = excelProperty.index();
            String[] values = excelProperty.value();
            StringBuilder value = new StringBuilder();
            for (String v : values) {
                value.append(v);
            }
            result.put(index, value.toString());
        }
        return result;
    }

    public List<T> getSuccessList() {
        return this.successList;
    }

    public void setSuccessList(List<T> successList) {
        this.successList = successList;
    }

    public List<ExcelCheckErrDto<T>> getErrList() {
        return this.errList;
    }

    public void setErrList(List<ExcelCheckErrDto<T>> errList) {
        this.errList = errList;
    }

    public ExcelCheckManager<T> getExcelCheckManager() {
        return this.excelCheckManager;
    }

    public void setExcelCheckManager(ExcelCheckManager<T> excelCheckManager) {
        this.excelCheckManager = excelCheckManager;
    }

    public List<T> getList() {
        return this.list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public Class<T> getClazz() {
        return this.clazz;
    }

    public void setClazz(Class<T> clazz) {
        this.clazz = clazz;
    }
}

