/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.alibaba.excel.context.AnalysisContext
 *  com.alibaba.excel.event.AnalysisEventListener
 */
package com.kuma.boot.office.easyexcel.easyexcelorigin;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExcelListener<T>
extends AnalysisEventListener<T> {
    private volatile boolean retryLock = false;
    private final List<T> dataList = new ArrayList<T>();
    private final int batchSize = 2000;

    public List<T> getDataList() {
        while (!this.retryLock) {
        }
        if (this.dataList.size() > 2000) {
            this.dataList.clear();
            throw new RuntimeException("\u4e00\u6b21\u6700\u591a\u5bfc\u51652000\u6761\u6570\u636e");
        }
        return this.dataList;
    }

    public void invoke(T data, AnalysisContext context) {
        this.dataList.add(data);
    }

    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
    }

    public void doAfterAllAnalysed(AnalysisContext context) {
        this.retryLock = true;
    }

    public void onException(Exception exception, AnalysisContext context) {
        throw new RuntimeException("Excel\u6570\u636e\u5f02\u5e38\uff0c\u8bf7\u68c0\u67e5\u6216\u8054\u7cfb\u7ba1\u7406\u5458\uff01");
    }

    public int getBatchSize() {
        return 2000;
    }

    public void setRetryLock(boolean retryLock) {
        this.retryLock = retryLock;
    }

    public boolean isRetryLock() {
        return this.retryLock;
    }
}

