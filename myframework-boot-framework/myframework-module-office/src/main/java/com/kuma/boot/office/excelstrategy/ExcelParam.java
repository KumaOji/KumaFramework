package com.kuma.boot.office.excelstrategy;



import jakarta.servlet.http.HttpServletResponse;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 */
public class ExcelParam extends ExcelParamAbstract implements Serializable {

    private static final long serialVersionUID = -4231868339831975335L;

    /**
     * 反射具体类
     */
    private Class clazz;
    /**
     * 从第几行开始扫描
     */
    private Integer rowNumIndex;
    /**
     * 读取到第几个sheet结束
     */
    private Integer sheetIndex;
    /**
     * 读取指定的sheetName或写入指定的sheetName
     */
    private String sheetName;
    /**
     * 存储属性和表头的对应关系
     */
    private Map map;
    /**
     * keyValue
     */
    private String keyValue;
    /**
     * 表头是否强一致
     */
    private Boolean sameHeader = false;
    /**
     * 是否流读取
     */
    private Boolean stream = false;
    /**
     * 用流代替本地文件
     */
    private byte[] buf;

    /**
     * 表头
     */
    private String headerName;

    /**
     * 文件导出封装数据
     */
    private List list;
    /**
     *文件导出封装数据
     */
    private Object obj;

    /**
     * 水印文字
     */
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
    public ExcelParam(Class clazz, String outFilePath, List list,String headerName) {
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
    public ExcelParam(Class clazz, HttpServletResponse response,List list,String headerName) {
        this.clazz = clazz;
        this.response = response;
        this.list = list;
        this.headerName = headerName;
    }

    public ExcelParam(Class clazz, String keyValue, HttpServletResponse response,List list) {
        this.clazz = clazz;
        this.keyValue = keyValue;
        this.response = response;
        this.list = list;
    }

    public ExcelParam(Class clazz, String keyValue, HttpServletResponse response, String fileName,List list) {
        this.clazz = clazz;
        this.keyValue = keyValue;
        this.response = response;
        this.fileName = fileName;
        this.headerName = fileName;
        this.list = list;
    }

    public ExcelParam(Class clazz, String keyValue, HttpServletResponse response, String fileName,Boolean fileNameAsHeaderName,List list) {
        this.clazz = clazz;
        this.keyValue = keyValue;
        this.response = response;
        this.fileName = fileName;
        if(fileNameAsHeaderName) {
            this.headerName = fileName;
        }
        this.list = list;
    }

    public ExcelParam(Class clazz, HttpServletResponse response, String fileName,List list) {
        this.clazz = clazz;
        this.response = response;
        this.fileName = fileName;
        this.list = list;
    }
    public ExcelParam(Class clazz, HttpServletResponse response, String fileName,Boolean fileNameAsHeaderName,List list) {
        this.clazz = clazz;
        this.response = response;
        this.fileName = fileName;
        if(fileNameAsHeaderName) {
            this.headerName = fileName;
        }
        this.list = list;
    }

    public ExcelParam(HttpServletResponse response, String templatePath,String outFilePath,Object obj) {
        this.response = response;
        this.filePath = templatePath;
        this.outFilePath = outFilePath;
        this.obj = obj;
    }

    public ExcelParam(HttpServletResponse response, String templatePath,Object obj,String fileName) {
        this.response = response;
        this.filePath = templatePath;
        this.fileName = fileName;
        this.obj = obj;
    }


    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public Integer getRowNumIndex() {
        return rowNumIndex;
    }

    public void setRowNumIndex(Integer rowNumIndex) {
        this.rowNumIndex = rowNumIndex;
    }

    public Integer getSheetIndex() {
        return sheetIndex;
    }

    public void setSheetIndex(Integer sheetIndex) {
        this.sheetIndex = sheetIndex;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public Boolean getSameHeader() {
        return sameHeader;
    }

    public void setSameHeader(Boolean sameHeader) {
        this.sameHeader = sameHeader;
    }

    public Boolean getStream() {
        return stream;
    }

    public void setStream(Boolean stream) {
        this.stream = stream;
    }

    public byte[] getBuf() {
        return buf;
    }

    public void setBuf(byte[] buf) {
        this.buf = buf;
    }

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    @Override
    public List getList() {
        return list;
    }

    @Override
    public void setList(List list) {
        this.list = list;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public String getWaterMark() {
        return waterMark;
    }

    public void setWaterMark(String waterMark) {
        this.waterMark = waterMark;
    }
}
