/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  io.swagger.v3.oas.annotations.media.Schema
 */
package com.kuma.boot.common.model.result;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;

@Schema(description="\u5206\u9875\u7ed3\u679c\u5bf9\u8c61")
public class PageResultMulti<R>
implements Serializable {
    private static final long serialVersionUID = -275582248840137389L;
    @Schema(description="\u603b\u6761\u6570")
    private long totalSize;
    @Schema(description="\u603b\u9875\u6570")
    private int totalPage;
    @Schema(description="\u5f53\u524d\u7b2c\u51e0\u9875")
    private int currentPage;
    @Schema(description="\u6bcf\u9875\u663e\u793a\u6761\u6570")
    private int pageSize;
    @Schema(description="\u8fd4\u56de\u6570\u636e")
    private List<R> data;

    public PageResultMulti() {
    }

    public PageResultMulti(long totalSize, int totalPage, int currentPage, int pageSize, List<R> data) {
        this.totalSize = totalSize;
        this.totalPage = totalPage;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.data = data;
    }

    public static <R> PageResultMulti<R> of(long totalSize, int totalPage, int currentPage, int pageSize, List<R> data) {
        return (PageResultMulti<R>) PageResultMulti.builder().totalSize(totalSize).totalPage(totalPage).currentPage(currentPage).pageSize(pageSize).data((List<Object>) data).build();
    }

    public long getTotalSize() {
        return this.totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public int getTotalPage() {
        return this.totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<R> getData() {
        return this.data;
    }

    public void setData(List<R> data) {
        this.data = data;
    }

    public static <R> PageModelBuilder<R> builder() {
        return new PageModelBuilder();
    }

    public static final class PageModelBuilder<R> {
        private long totalSize;
        private int totalPage;
        private int currentPage;
        private int pageSize;
        private List<R> data;

        private PageModelBuilder() {
        }

        public PageModelBuilder<R> totalSize(long totalSize) {
            this.totalSize = totalSize;
            return this;
        }

        public PageModelBuilder<R> totalPage(int totalPage) {
            this.totalPage = totalPage;
            return this;
        }

        public PageModelBuilder<R> currentPage(int currentPage) {
            this.currentPage = currentPage;
            return this;
        }

        public PageModelBuilder<R> pageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public PageModelBuilder<R> data(List<R> data) {
            this.data = data;
            return this;
        }

        public PageResultMulti<R> build() {
            PageResultMulti<R> pageModel = new PageResultMulti<R>();
            pageModel.setTotalSize(this.totalSize);
            pageModel.setTotalPage(this.totalPage);
            pageModel.setCurrentPage(this.currentPage);
            pageModel.setPageSize(this.pageSize);
            pageModel.setData(this.data);
            return pageModel;
        }
    }
}

