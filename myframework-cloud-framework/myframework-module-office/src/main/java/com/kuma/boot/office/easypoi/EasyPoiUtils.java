/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.afterturn.easypoi.excel.ExcelExportUtil
 *  cn.afterturn.easypoi.excel.ExcelImportUtil
 *  cn.afterturn.easypoi.excel.entity.ExportParams
 *  cn.afterturn.easypoi.excel.entity.ImportParams
 *  cn.afterturn.easypoi.excel.entity.enmus.ExcelType
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  jakarta.servlet.http.HttpServletResponse
 *  org.apache.poi.ss.usermodel.Workbook
 *  org.springframework.web.multipart.MultipartFile
 */
package com.kuma.boot.office.easypoi;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.kuma.boot.common.utils.lang.StringUtils;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

public final class EasyPoiUtils {
    private EasyPoiUtils() {
    }

    public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass, String fileName, boolean isCreateHeader, HttpServletResponse response) throws IOException {
        ExportParams exportParams = new ExportParams(title, sheetName, ExcelType.XSSF);
        exportParams.setCreateHeadRows(isCreateHeader);
        EasyPoiUtils.defaultExport(list, pojoClass, fileName, response, exportParams);
    }

    public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass, String fileName, HttpServletResponse response) throws IOException {
        EasyPoiUtils.defaultExport(list, pojoClass, fileName, response, new ExportParams(title, sheetName, ExcelType.XSSF));
    }

    public static void exportExcel(List<?> list, Class<?> pojoClass, String fileName, ExportParams exportParams, HttpServletResponse response) throws IOException {
        EasyPoiUtils.defaultExport(list, pojoClass, fileName, response, exportParams);
    }

    public static void exportExcel(List<Map<String, Object>> list, String fileName, HttpServletResponse response) throws IOException {
        EasyPoiUtils.defaultExport(list, fileName, response);
    }

    private static void defaultExport(List<?> list, Class<?> pojoClass, String fileName, HttpServletResponse response, ExportParams exportParams) throws IOException {
        Workbook workbook = ExcelExportUtil.exportExcel((ExportParams)exportParams, pojoClass, list);
        EasyPoiUtils.downLoadExcel(fileName, response, workbook);
    }

    private static void defaultExport(List<Map<String, Object>> list, String fileName, HttpServletResponse response) throws IOException {
        Workbook workbook = ExcelExportUtil.exportExcel(list, (ExcelType)ExcelType.HSSF);
        EasyPoiUtils.downLoadExcel(fileName, response, workbook);
    }

    private static void downLoadExcel(String fileName, HttpServletResponse response, Workbook workbook) throws IOException {
        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName + "." + ExcelTypeEnum.XLSX.getValue(), String.valueOf(StandardCharsets.UTF_8)));
            workbook.write((OutputStream)response.getOutputStream());
        }
        catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    public static <T> List<T> importExcel(String filePath, Integer titleRows, Integer headerRows, Class<T> pojoClass) throws IOException {
        if (StringUtils.isBlank((String)filePath)) {
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows.intValue());
        params.setHeadRows(headerRows.intValue());
        params.setNeedSave(true);
        params.setSaveUrl("/excel/");
        try {
            return ExcelImportUtil.importExcel((File)new File(filePath), pojoClass, (ImportParams)params);
        }
        catch (NoSuchElementException e) {
            throw new IOException("\u6a21\u677f\u4e0d\u80fd\u4e3a\u7a7a");
        }
        catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    public static <T> List<T> importExcel(MultipartFile file, Class<T> pojoClass) throws IOException {
        return EasyPoiUtils.importExcel(file, (Integer)1, (Integer)1, pojoClass);
    }

    public static <T> List<T> importExcel(MultipartFile file, Integer titleRows, Integer headerRows, Class<T> pojoClass) throws IOException {
        return EasyPoiUtils.importExcel(file, titleRows, headerRows, false, pojoClass);
    }

    public static <T> List<T> importExcel(MultipartFile file, Integer titleRows, Integer headerRows, boolean needVerfiy, Class<T> pojoClass) throws IOException {
        if (file == null) {
            return null;
        }
        try {
            return EasyPoiUtils.importExcel(file.getInputStream(), titleRows, headerRows, needVerfiy, pojoClass);
        }
        catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    public static <T> List<T> importExcel(InputStream inputStream, Integer titleRows, Integer headerRows, boolean needVerfiy, Class<T> pojoClass) throws IOException {
        if (inputStream == null) {
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows.intValue());
        params.setHeadRows(headerRows.intValue());
        params.setSaveUrl("/excel/");
        params.setNeedSave(true);
        try {
            return ExcelImportUtil.importExcel((InputStream)inputStream, pojoClass, (ImportParams)params);
        }
        catch (NoSuchElementException e) {
            throw new IOException("excel\u6587\u4ef6\u4e0d\u80fd\u4e3a\u7a7a");
        }
        catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    static enum ExcelTypeEnum {
        XLS("xls"),
        XLSX("xlsx");

        private String value;

        private ExcelTypeEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}

