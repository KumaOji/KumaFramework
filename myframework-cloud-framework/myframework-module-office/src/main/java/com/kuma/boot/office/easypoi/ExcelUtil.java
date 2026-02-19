/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.afterturn.easypoi.excel.ExcelExportUtil
 *  cn.afterturn.easypoi.excel.ExcelImportUtil
 *  cn.afterturn.easypoi.excel.entity.ExportParams
 *  cn.afterturn.easypoi.excel.entity.ImportParams
 *  cn.afterturn.easypoi.excel.entity.enmus.ExcelType
 *  com.kuma.boot.common.exception.BusinessException
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  jakarta.servlet.http.HttpServletResponse
 *  org.apache.poi.ss.usermodel.Workbook
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.web.multipart.MultipartFile
 */
package com.kuma.boot.office.easypoi;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.utils.lang.StringUtils;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

public class ExcelUtil {
    private static final Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

    private ExcelUtil() {
    }

    public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass, String fileName, boolean isCreateHeader, HttpServletResponse response) {
        ExportParams exportParams = new ExportParams(title, sheetName);
        exportParams.setCreateHeadRows(isCreateHeader);
        ExcelUtil.defaultExport(list, pojoClass, fileName, response, exportParams);
    }

    public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass, String fileName, HttpServletResponse response) {
        ExcelUtil.defaultExport(list, pojoClass, fileName, response, new ExportParams(title, sheetName));
    }

    public static void exportExcel(List<Map<String, Object>> list, String fileName, HttpServletResponse response) {
        ExcelUtil.defaultExport(list, fileName, response);
    }

    private static void defaultExport(List<?> list, Class<?> pojoClass, String fileName, HttpServletResponse response, ExportParams exportParams) {
        Workbook workbook = ExcelExportUtil.exportExcel((ExportParams)exportParams, pojoClass, list);
        if (workbook != null) {
            ExcelUtil.downLoadExcel(fileName, response, workbook);
        }
    }

    private static void downLoadExcel(String fileName, HttpServletResponse response, Workbook workbook) {
        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            workbook.write((OutputStream)response.getOutputStream());
        }
        catch (IOException e) {
            logger.error("excel\u6587\u6863\u5bfc\u51fa\u4e0b\u8f7d\u62a5\u9519:{}", (Throwable)e);
            throw new BusinessException("excel\u4e0b\u8f7d\u62a5\u9519");
        }
    }

    private static void defaultExport(List<Map<String, Object>> list, String fileName, HttpServletResponse response) {
        Workbook workbook = ExcelExportUtil.exportExcel(list, (ExcelType)ExcelType.HSSF);
        if (workbook != null) {
            ExcelUtil.downLoadExcel(fileName, response, workbook);
        }
    }

    public static <T> List<T> importExcel(String filePath, Integer titleRows, Integer headerRows, Class<T> pojoClass) {
        List list;
        if (StringUtils.isBlank((String)filePath)) {
            return Collections.emptyList();
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows.intValue());
        params.setHeadRows(headerRows.intValue());
        try {
            list = ExcelImportUtil.importExcel((File)new File(filePath), pojoClass, (ImportParams)params);
        }
        catch (NoSuchElementException e) {
            logger.error("excel\u6a21\u677f\u6587\u4ef6\u5bfc\u5165\u4e3a\u7a7a:{}", (Throwable)e);
            throw new BusinessException("excel\u4e0b\u8f7d\u62a5\u9519");
        }
        catch (Exception e) {
            logger.error("excel\u6a21\u677f\u6587\u4ef6\u5bfc\u5165\u4e3a\u7a7a:{}", (Throwable)e);
            throw new BusinessException("excel\u4e0b\u8f7d\u62a5\u9519");
        }
        return list;
    }

    public static <T> List<T> importExcel(MultipartFile file, Integer titleRows, Integer headerRows, Class<T> pojoClass) {
        if (file == null) {
            return Collections.emptyList();
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows.intValue());
        params.setHeadRows(headerRows.intValue());
        List list = null;
        try {
            list = ExcelImportUtil.importExcel((InputStream)file.getInputStream(), pojoClass, (ImportParams)params);
        }
        catch (NoSuchElementException e) {
            logger.error("excel\u6587\u4ef6\u5bfc\u5165\u4e3a\u7a7a:{}", (Throwable)e);
            throw new BusinessException("excel\u4e0b\u8f7d\u62a5\u9519");
        }
        catch (Exception e) {
            logger.error("excel\u5bfc\u5165\u4e3a\u7a7a:{}", (Throwable)e);
            throw new BusinessException("excel\u4e0b\u8f7d\u62a5\u9519");
        }
        return list;
    }

    public static <T> List<T> importExcel(InputStream inputStream, Integer titleRows, Integer headerRows, ImportParams params, Class<T> pojoClass) throws IOException {
        if (inputStream == null) {
            return Collections.emptyList();
        }
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
            throw new IOException(e);
        }
    }
}

