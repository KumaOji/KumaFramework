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

public class ExcelUtil
extends ExcelUtilBase
implements Serializable {
    private static final long serialVersionUID = 1L;

    public static <T> List<T> readXls(String filePath, Map map, Class clazz) throws Exception {
        ExcelParam excelParam = new ExcelParam();
        excelParam.setFilePath(filePath);
        excelParam.setMap(map);
        excelParam.setClazz(clazz);
        return ExcelUtil.getResult(excelParam);
    }

    public static <T> List<T> readXls(String filePath, Class clazz) throws Exception {
        ExcelParam excelParam = new ExcelParam();
        excelParam.setFilePath(filePath);
        excelParam.setClazz(clazz);
        return ExcelUtil.getResult(excelParam);
    }

    public static <T> List<T> readXls(String filePath, Class clazz, int ... rowNumIndex) throws Exception {
        ExcelParam excelParam = new ExcelParam();
        excelParam.setFilePath(filePath);
        excelParam.setClazz(clazz);
        if (rowNumIndex.length > 0) {
            excelParam.setRowNumIndex(rowNumIndex[0]);
        }
        return ExcelUtil.getResult(excelParam);
    }

    public static <T> List<T> readXls(byte[] buf, Map map, Class<T> clazz, int ... rowNumIndex) throws Exception {
        ExcelParam excelParam = new ExcelParam();
        excelParam.setMap(map);
        excelParam.setClazz(clazz);
        excelParam.setBuf(buf);
        if (rowNumIndex.length > 0) {
            excelParam.setRowNumIndex(rowNumIndex[0]);
        }
        return ExcelUtil.getResult(excelParam);
    }

    public static <T> List<T> readXls(byte[] buf, Class<T> clazz, int ... rowNumIndex) throws Exception {
        ExcelParam excelParam = new ExcelParam();
        excelParam.setClazz(clazz);
        excelParam.setStream(true);
        excelParam.setBuf(buf);
        if (rowNumIndex.length > 0) {
            excelParam.setRowNumIndex(rowNumIndex[0]);
        }
        return ExcelUtil.getResult(excelParam);
    }

    public static void exportExcel(String outFilePath, String keyValue, List<?> list, Class clazz) throws Exception {
        ExcelParam excelParam = new ExcelParam(clazz, keyValue, outFilePath, list);
        ExcelUtil.commonExportExcel(excelParam);
    }

    public static void exportExcel(String outFilePath, List<?> list, Class clazz) throws Exception {
        ExcelParam excelParam = new ExcelParam(clazz, outFilePath, list);
        ExcelUtil.commonExportExcel(excelParam);
    }

    public static void exportExcel(String outFilePath, List<?> list, Class clazz, String headerName, String waterMark) throws Exception {
        ExcelParam excelParam = new ExcelParam(clazz, outFilePath, list, headerName);
        excelParam.setWaterMark(waterMark);
        ExcelUtil.commonExportExcel(excelParam);
    }

    public static void exportExcelOutputStream(HttpServletResponse response, String keyValue, List<?> list, Class clazz) throws Exception {
        ExcelParam excelParam = new ExcelParam(clazz, keyValue, response, list);
        ExcelUtil.commonExportExcel(excelParam);
    }

    public static void exportExcelOutputStream(HttpServletResponse response, String keyValue, List<?> list, Class clazz, String fileName) throws Exception {
        ExcelParam excelParam = new ExcelParam(clazz, keyValue, response, fileName, list);
        ExcelUtil.commonExportExcel(excelParam);
    }

    public static void exportExcelOutputStream(HttpServletResponse response, String keyValue, List<?> list, Class clazz, String fileName, Boolean fileNameAsHeaderName) throws Exception {
        ExcelParam excelParam = new ExcelParam(clazz, keyValue, response, fileName, fileNameAsHeaderName, list);
        ExcelUtil.commonExportExcel(excelParam);
    }

    public static void exportExcelOutputStream(HttpServletResponse response, List<?> list, Class clazz) throws Exception {
        ExcelParam excelParam = new ExcelParam(clazz, response, list);
        ExcelUtil.commonExportExcel(excelParam);
    }

    public static void exportExcelOutputStream(HttpServletResponse response, List<?> list, Class clazz, String fileName) throws Exception {
        ExcelParam excelParam = new ExcelParam(clazz, response, fileName, list);
        ExcelUtil.commonExportExcel(excelParam);
    }

    public static void exportExcelOutputStream(HttpServletResponse response, List<?> list, Class clazz, String fileName, String waterMark) throws Exception {
        ExcelParam excelParam = new ExcelParam(clazz, response, fileName, list);
        excelParam.setWaterMark(waterMark);
        ExcelUtil.commonExportExcel(excelParam);
    }

    public static void exportExcelOutputStream(ExcelParamAbstract excelParamAbstract) throws Exception {
        ExcelUtil.commonExportExcel2(excelParamAbstract);
    }

    public static void exportExcelOutputStream(HttpServletResponse response, List<?> list, Class clazz, String fileName, Boolean fileNameAsHeaderName) throws Exception {
        ExcelParam excelParam = new ExcelParam(clazz, response, fileName, fileNameAsHeaderName, list);
        ExcelUtil.commonExportExcel(excelParam);
    }

    public static void templateWrite(HttpServletResponse response, String templatePath, String outFilePath, Object obj) {
        ExcelParam excelParam = new ExcelParam(response, templatePath, outFilePath, obj);
        ExcelUtil.templateWrite(excelParam);
    }

    public static void templateWrite(HttpServletResponse response, String templatePath, Object obj, String fileName) {
        ExcelParam excelParam = new ExcelParam(response, templatePath, obj, fileName);
        ExcelUtil.templateWrite(excelParam);
    }
}

