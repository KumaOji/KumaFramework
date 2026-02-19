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
import java.util.Map;

public class ExcelParam
extends ExcelParamAbstract
implements Serializable {
    private static final long serialVersionUID = -4231868339831975335L;
    private Class clazz;
    private Integer rowNumIndex;
    private Integer sheetIndex;
    private String sheetName;
    private Map map;
    private String keyValue;
    private Boolean sameHeader = false;
    private Boolean stream = false;
    private byte[] buf;
    private String headerName;
    private List list;
    private Object obj;
    private String waterMark;

    public ExcelParam() {
    }

    public ExcelParam(Class clazz, String keyValue, String outFilePath, List list) {
        this.clazz = clazz;
        this.keyValue = keyValue;
        this.outFilePath = outFilePath;
        this.list = list;
    }

    public ExcelParam(Class clazz, String outFilePath, List list) {
        this.clazz = clazz;
        this.outFilePath = outFilePath;
        this.list = list;
    }

    public ExcelParam(Class clazz, String outFilePath, List list, String headerName) {
        this.clazz = clazz;
        this.outFilePath = outFilePath;
        this.list = list;
        this.headerName = headerName;
    }

    public ExcelParam(Class clazz, HttpServletResponse response, List list) {
        this.clazz = clazz;
        this.response = response;
        this.list = list;
    }

    public ExcelParam(Class clazz, HttpServletResponse response, List list, String headerName) {
        this.clazz = clazz;
        this.response = response;
        this.list = list;
        this.headerName = headerName;
    }

    public ExcelParam(Class clazz, String keyValue, HttpServletResponse response, List list) {
        this.clazz = clazz;
        this.keyValue = keyValue;
        this.response = response;
        this.list = list;
    }

    public ExcelParam(Class clazz, String keyValue, HttpServletResponse response, String fileName, List list) {
        this.clazz = clazz;
        this.keyValue = keyValue;
        this.response = response;
        this.fileName = fileName;
        this.headerName = fileName;
        this.list = list;
    }

    public ExcelParam(Class clazz, String keyValue, HttpServletResponse response, String fileName, Boolean fileNameAsHeaderName, List list) {
        this.clazz = clazz;
        this.keyValue = keyValue;
        this.response = response;
        this.fileName = fileName;
        if (fileNameAsHeaderName.booleanValue()) {
            this.headerName = fileName;
        }
        this.list = list;
    }

    public ExcelParam(Class clazz, HttpServletResponse response, String fileName, List list) {
        this.clazz = clazz;
        this.response = response;
        this.fileName = fileName;
        this.list = list;
    }

    public ExcelParam(Class clazz, HttpServletResponse response, String fileName, Boolean fileNameAsHeaderName, List list) {
        this.clazz = clazz;
        this.response = response;
        this.fileName = fileName;
        if (fileNameAsHeaderName.booleanValue()) {
            this.headerName = fileName;
        }
        this.list = list;
    }

    public ExcelParam(HttpServletResponse response, String templatePath, String outFilePath, Object obj) {
        this.response = response;
        this.filePath = templatePath;
        this.outFilePath = outFilePath;
        this.obj = obj;
    }

    public ExcelParam(HttpServletResponse response, String templatePath, Object obj, String fileName) {
        this.response = response;
        this.filePath = templatePath;
        this.fileName = fileName;
        this.obj = obj;
    }

    public Class getClazz() {
        return this.clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public Integer getRowNumIndex() {
        return this.rowNumIndex;
    }

    public void setRowNumIndex(Integer rowNumIndex) {
        this.rowNumIndex = rowNumIndex;
    }

    public Integer getSheetIndex() {
        return this.sheetIndex;
    }

    public void setSheetIndex(Integer sheetIndex) {
        this.sheetIndex = sheetIndex;
    }

    public String getSheetName() {
        return this.sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public Map getMap() {
        return this.map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public String getKeyValue() {
        return this.keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public Boolean getSameHeader() {
        return this.sameHeader;
    }

    public void setSameHeader(Boolean sameHeader) {
        this.sameHeader = sameHeader;
    }

    public Boolean getStream() {
        return this.stream;
    }

    public void setStream(Boolean stream) {
        this.stream = stream;
    }

    public byte[] getBuf() {
        return this.buf;
    }

    public void setBuf(byte[] buf) {
        this.buf = buf;
    }

    public String getHeaderName() {
        return this.headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    public List getList() {
        return this.list;
    }

    public void setList(List list) {
        this.list = list;
    }

    public Object getObj() {
        return this.obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public String getWaterMark() {
        return this.waterMark;
    }

    public void setWaterMark(String waterMark) {
        this.waterMark = waterMark;
    }
}

