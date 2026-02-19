/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.collection.CollUtil
 *  cn.hutool.core.io.resource.ClassPathResource
 *  cn.hutool.core.util.IdUtil
 *  com.alibaba.excel.EasyExcel
 *  com.alibaba.excel.ExcelWriter
 *  com.alibaba.excel.converters.Converter
 *  com.alibaba.excel.read.builder.ExcelReaderBuilder
 *  com.alibaba.excel.write.builder.ExcelWriterBuilder
 *  com.alibaba.excel.write.builder.ExcelWriterSheetBuilder
 *  com.alibaba.excel.write.handler.WriteHandler
 *  com.alibaba.excel.write.metadata.WriteSheet
 *  com.alibaba.excel.write.metadata.fill.FillConfig
 *  com.alibaba.excel.write.metadata.fill.FillWrapper
 *  com.alibaba.excel.write.metadata.style.WriteCellStyle
 *  com.alibaba.excel.write.metadata.style.WriteFont
 *  com.alibaba.excel.write.style.HorizontalCellStyleStrategy
 *  com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  jakarta.servlet.ServletOutputStream
 *  jakarta.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.poi.ss.usermodel.BorderStyle
 *  org.apache.poi.ss.usermodel.FillPatternType
 *  org.apache.poi.ss.usermodel.HorizontalAlignment
 *  org.apache.poi.ss.usermodel.IndexedColors
 */
package com.kuma.boot.office.easyexcel.easyexcelconvert;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.IdUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.handler.WriteHandler;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.excel.write.metadata.fill.FillWrapper;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.kuma.boot.office.easyexcel.easyexcelconvert.convert.ExcelBigNumberConvert;
import com.kuma.boot.office.easyexcel.easyexcelconvert.core.CellMergeStrategy;
import com.kuma.boot.office.easyexcel.easyexcelconvert.core.DefaultExcelListener;
import com.kuma.boot.office.easyexcel.easyexcelconvert.core.ExcelListener;
import com.kuma.boot.office.easyexcel.easyexcelconvert.core.ExcelResult;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;

public class EasyExcelUtils {
    public static <T> List<T> importExcel(InputStream is, Class<T> clazz) {
        return ((ExcelReaderBuilder)EasyExcel.read((InputStream)is).head(clazz)).autoCloseStream(Boolean.valueOf(false)).sheet().doReadSync();
    }

    public static <T> ExcelResult<T> importExcel(InputStream is, Class<T> clazz, boolean isValidate) {
        DefaultExcelListener listener = new DefaultExcelListener(isValidate);
        EasyExcel.read((InputStream)is, clazz, listener).sheet().doRead();
        return listener.getExcelResult();
    }

    public static <T> ExcelResult<T> importExcel(InputStream is, Class<T> clazz, ExcelListener<T> listener) {
        EasyExcel.read((InputStream)is, clazz, listener).sheet().doRead();
        return listener.getExcelResult();
    }

    public static <T> void exportExcel(List<T> list, String sheetName, Class<T> clazz, HttpServletResponse response) {
        EasyExcelUtils.exportExcel(list, sheetName, clazz, false, response);
    }

    public static <T> void exportExcel(List<T> list, String sheetName, Class<T> clazz, boolean merge, HttpServletResponse response) {
        try {
            EasyExcelUtils.resetResponse(sheetName, response);
            ServletOutputStream os = response.getOutputStream();
            ExcelWriterSheetBuilder builder = ((ExcelWriterBuilder)((ExcelWriterBuilder)EasyExcel.write((OutputStream)os, clazz).autoCloseStream(Boolean.valueOf(false)).registerWriteHandler((WriteHandler)new LongestMatchColumnWidthStyleStrategy())).registerConverter((Converter)new ExcelBigNumberConvert())).sheet(sheetName);
            if (merge) {
                builder.registerWriteHandler((WriteHandler)new CellMergeStrategy(list, true));
            }
            builder.doWrite(list);
        }
        catch (IOException e) {
            throw new RuntimeException("\u5bfc\u51faExcel\u5f02\u5e38");
        }
    }

    public static void exportTemplate(List<Object> data, String filename, String templatePath, HttpServletResponse response) {
        try {
            EasyExcelUtils.resetResponse(filename, response);
            ClassPathResource templateResource = new ClassPathResource(templatePath);
            ExcelWriter excelWriter = ((ExcelWriterBuilder)EasyExcel.write((OutputStream)response.getOutputStream()).withTemplate(templateResource.getStream()).autoCloseStream(Boolean.valueOf(false)).registerConverter((Converter)new ExcelBigNumberConvert())).build();
            WriteSheet writeSheet = EasyExcel.writerSheet().build();
            if (CollUtil.isEmpty(data)) {
                throw new IllegalArgumentException("\u6570\u636e\u4e3a\u7a7a");
            }
            for (Object d : data) {
                excelWriter.fill(d, writeSheet);
            }
            excelWriter.finish();
        }
        catch (IOException e) {
            throw new RuntimeException("\u5bfc\u51faExcel\u5f02\u5e38");
        }
    }

    public static void exportTemplateMultiList(Map<String, Object> data, String filename, String templatePath, HttpServletResponse response) {
        try {
            EasyExcelUtils.resetResponse(filename, response);
            ClassPathResource templateResource = new ClassPathResource(templatePath);
            ExcelWriter excelWriter = ((ExcelWriterBuilder)EasyExcel.write((OutputStream)response.getOutputStream()).withTemplate(templateResource.getStream()).autoCloseStream(Boolean.valueOf(false)).registerConverter((Converter)new ExcelBigNumberConvert())).build();
            WriteSheet writeSheet = EasyExcel.writerSheet().build();
            if (CollUtil.isEmpty(data)) {
                throw new IllegalArgumentException("\u6570\u636e\u4e3a\u7a7a");
            }
            for (Map.Entry<String, Object> map : data.entrySet()) {
                FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
                if (map.getValue() instanceof Collection) {
                    excelWriter.fill((Object)new FillWrapper(map.getKey(), (Collection)map.getValue()), fillConfig, writeSheet);
                    continue;
                }
                excelWriter.fill(map.getValue(), writeSheet);
            }
            excelWriter.finish();
        }
        catch (IOException e) {
            throw new RuntimeException("\u5bfc\u51faExcel\u5f02\u5e38");
        }
    }

    private static void resetResponse(String sheetName, HttpServletResponse response) throws UnsupportedEncodingException {
        String filename = EasyExcelUtils.encodingFilename(sheetName);
        response.reset();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
    }

    public static String convertByExp(String propertyValue, String converterExp, String separator) {
        String[] convertSource;
        StringBuilder propertyString = new StringBuilder();
        block0: for (String item : convertSource = converterExp.split(",")) {
            String[] itemArray = item.split("=");
            if (com.kuma.boot.common.utils.lang.StringUtils.containsAny((CharSequence)propertyValue, (CharSequence[])new CharSequence[]{separator})) {
                for (String value : propertyValue.split(separator)) {
                    if (!itemArray[0].equals(value)) continue;
                    propertyString.append(itemArray[1]).append(separator);
                    continue block0;
                }
                continue;
            }
            if (!itemArray[0].equals(propertyValue)) continue;
            return itemArray[1];
        }
        return StringUtils.stripEnd((String)propertyString.toString(), (String)separator);
    }

    public static String reverseByExp(String propertyValue, String converterExp, String separator) {
        String[] convertSource;
        StringBuilder propertyString = new StringBuilder();
        block0: for (String item : convertSource = converterExp.split(",")) {
            String[] itemArray = item.split("=");
            if (com.kuma.boot.common.utils.lang.StringUtils.containsAny((CharSequence)propertyValue, (CharSequence[])new CharSequence[]{separator})) {
                for (String value : propertyValue.split(separator)) {
                    if (!itemArray[1].equals(value)) continue;
                    propertyString.append(itemArray[0]).append(separator);
                    continue block0;
                }
                continue;
            }
            if (!itemArray[1].equals(propertyValue)) continue;
            return itemArray[0];
        }
        return StringUtils.stripEnd((String)propertyString.toString(), (String)separator);
    }

    public static String encodingFilename(String filename) {
        return IdUtil.fastSimpleUUID() + "_" + filename + ".xlsx";
    }

    public static HorizontalCellStyleStrategy getHorizontalCellStyleStrategy() {
        WriteCellStyle headerCellStyle = new WriteCellStyle();
        headerCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        headerCellStyle.setFillForegroundColor(Short.valueOf(IndexedColors.SKY_BLUE.getIndex()));
        WriteFont headerFont = new WriteFont();
        headerFont.setFontHeightInPoints(Short.valueOf((short)15));
        headerCellStyle.setWriteFont(headerFont);
        headerCellStyle.setWrapped(Boolean.FALSE);
        WriteCellStyle contentCellStyle = new WriteCellStyle();
        contentCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        contentCellStyle.setFillForegroundColor(Short.valueOf(IndexedColors.GREY_40_PERCENT.getIndex()));
        contentCellStyle.setHorizontalAlignment(HorizontalAlignment.LEFT);
        WriteFont contentFont = new WriteFont();
        contentFont.setFontHeightInPoints(Short.valueOf((short)12));
        contentCellStyle.setWriteFont(contentFont);
        contentCellStyle.setWrapped(Boolean.FALSE);
        contentCellStyle.setBorderLeft(BorderStyle.MEDIUM);
        contentCellStyle.setBorderTop(BorderStyle.MEDIUM);
        contentCellStyle.setBorderRight(BorderStyle.MEDIUM);
        contentCellStyle.setBorderBottom(BorderStyle.MEDIUM);
        contentCellStyle.setTopBorderColor(Short.valueOf(IndexedColors.RED.getIndex()));
        contentCellStyle.setBottomBorderColor(Short.valueOf(IndexedColors.GREEN.getIndex()));
        contentCellStyle.setLeftBorderColor(Short.valueOf(IndexedColors.YELLOW.getIndex()));
        contentCellStyle.setRightBorderColor(Short.valueOf(IndexedColors.ORANGE.getIndex()));
        return new HorizontalCellStyleStrategy(headerCellStyle, contentCellStyle);
    }
}

