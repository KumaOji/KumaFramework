/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.gracefulresponse.data;

import java.util.List;

public class PageBean<T> {
    private Integer pageSize;
    private Integer total;
    private Integer page;
    private List<T> list;

    public Integer getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotal() {
        return this.total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getPage() {
        return this.page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public List<T> getList() {
        return this.list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}

