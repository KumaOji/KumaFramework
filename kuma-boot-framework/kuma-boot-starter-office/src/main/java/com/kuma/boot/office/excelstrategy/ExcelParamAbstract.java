package com.kuma.boot.office.excelstrategy;


import jakarta.servlet.http.HttpServletResponse;

import java.io.Serializable;
import java.util.List;

/**
 */
public abstract class ExcelParamAbstract implements Serializable {

    /**
     * 	文件地址,本地读取时用
     */
    protected String filePath;

    /**
     * 输出流
     */
    protected HttpServletResponse response;

    /**
     * 文件名
     */
    protected String fileName;

    /**
     * 表头
     */
    protected Boolean fileNameAsHeadName;
    /**
     * 文件输出路径
     */
    protected String outFilePath;

    /**
     * list params
     */
    protected List<com.kuma.boot.office.excelstrategy.ExcelParam> list;


    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Boolean getFileNameAsHeadName() {
        return fileNameAsHeadName;
    }

    public void setFileNameAsHeadName(Boolean fileNameAsHeadName) {
        this.fileNameAsHeadName = fileNameAsHeadName;
    }

    public String getOutFilePath() {
        return outFilePath;
    }

    public void setOutFilePath(String outFilePath) {
        this.outFilePath = outFilePath;
    }

    public List<com.kuma.boot.office.excelstrategy.ExcelParam> getList() {
        return list;
    }

    public void setList(List<com.kuma.boot.office.excelstrategy.ExcelParam> list) {
        this.list = list;
    }
}
