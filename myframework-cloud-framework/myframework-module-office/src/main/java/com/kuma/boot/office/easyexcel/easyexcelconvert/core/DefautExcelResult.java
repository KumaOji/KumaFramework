/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 */
package com.kuma.boot.office.easyexcel.easyexcelconvert.core;

import cn.hutool.core.util.StrUtil;

import java.util.ArrayList;
import java.util.List;

public class DefautExcelResult<T>
implements ExcelResult<T> {
    private List<T> list;
    private List<String> errorList;

    public DefautExcelResult() {
        this.list = new ArrayList<T>();
        this.errorList = new ArrayList<String>();
    }

    public DefautExcelResult(List<T> list, List<String> errorList) {
        this.list = list;
        this.errorList = errorList;
    }

    public DefautExcelResult(ExcelResult<T> excelResult) {
        this.list = excelResult.getList();
        this.errorList = excelResult.getErrorList();
    }

    @Override
    public List<T> getList() {
        return this.list;
    }

    @Override
    public List<String> getErrorList() {
        return this.errorList;
    }

    @Override
    public String getAnalysis() {
        int successCount = this.list.size();
        int errorCount = this.errorList.size();
        if (successCount == 0) {
            return "\u8bfb\u53d6\u5931\u8d25\uff0c\u672a\u89e3\u6790\u5230\u6570\u636e";
        }
        if (errorCount == 0) {
            return StrUtil.format((CharSequence)"\u606d\u559c\u60a8\uff0c\u5168\u90e8\u8bfb\u53d6\u6210\u529f\uff01\u5171{}\u6761", (Object[])new Object[]{successCount});
        }
        return "";
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public void setErrorList(List<String> errorList) {
        this.errorList = errorList;
    }
}

