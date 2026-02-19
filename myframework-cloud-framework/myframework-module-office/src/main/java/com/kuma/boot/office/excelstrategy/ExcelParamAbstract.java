/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.servlet.http.HttpServletResponse
 */
package com.kuma.boot.office.excelstrategy;

import jakarta.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.List;

public abstract class ExcelParamAbstract
implements Serializable {
    protected String filePath;
    protected HttpServletResponse response;
    protected String fileName;
    protected Boolean fileNameAsHeadName;
    protected String outFilePath;
    protected List<ExcelParam> list;

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public HttpServletResponse getResponse() {
        return this.response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Boolean getFileNameAsHeadName() {
        return this.fileNameAsHeadName;
    }

    public void setFileNameAsHeadName(Boolean fileNameAsHeadName) {
        this.fileNameAsHeadName = fileNameAsHeadName;
    }

    public String getOutFilePath() {
        return this.outFilePath;
    }

    public void setOutFilePath(String outFilePath) {
        this.outFilePath = outFilePath;
    }

    public List<ExcelParam> getList() {
        return this.list;
    }

    public void setList(List<ExcelParam> list) {
        this.list = list;
    }
}

