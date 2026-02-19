/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.collection.CollUtil
 *  cn.hutool.core.io.resource.ResourceUtil
 *  cn.hutool.core.util.StrUtil
 *  com.alibaba.excel.EasyExcel
 *  com.alibaba.excel.ExcelWriter
 *  com.alibaba.excel.write.builder.ExcelWriterBuilder
 *  com.alibaba.excel.write.builder.ExcelWriterSheetBuilder
 *  com.alibaba.excel.write.handler.WriteHandler
 *  com.alibaba.excel.write.metadata.WriteSheet
 *  com.alibaba.excel.write.metadata.style.WriteCellStyle
 *  com.alibaba.excel.write.metadata.style.WriteFont
 *  com.alibaba.excel.write.style.HorizontalCellStyleStrategy
 *  com.alibaba.excel.write.style.column.SimpleColumnWidthStyleStrategy
 *  com.kuma.boot.common.utils.log.LogUtils
 *  jakarta.servlet.http.HttpServletResponse
 *  org.apache.poi.ss.usermodel.HorizontalAlignment
 */
package com.kuma.boot.office.easyexcel.easyexcelimport.excel;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.handler.WriteHandler;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.style.column.SimpleColumnWidthStyleStrategy;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.office.easyexcel.easyexcelimport.constant.ExportConstant;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

public class ExcelExportUtil<T> {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <T> void exportFile(String fileName, List<T> head, List<List<T>> exportData, List<String> sheetNames, HttpServletResponse response) {
        if (Objects.isNull(response) || StrUtil.isBlank((CharSequence)fileName) || CollUtil.isEmpty(head)) {
            LogUtils.info((String)"ExcelExportUtil exportFile required param can't be empty", (Object[])new Object[0]);
            return;
        }
        ExcelWriter writer = null;
        try {
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            HorizontalCellStyleStrategy horizontalCellStyleStrategy = ExcelExportUtil.getExportDefaultStyle();
            writer = ((ExcelWriterBuilder)EasyExcel.write((OutputStream)response.getOutputStream()).registerWriteHandler((WriteHandler)horizontalCellStyleStrategy)).build();
            for (int itemIndex = 0; itemIndex < exportData.size(); ++itemIndex) {
                T headData = head.get(itemIndex);
                List<T> list = exportData.get(itemIndex);
                WriteSheet sheet = ((ExcelWriterSheetBuilder)EasyExcel.writerSheet((Integer)itemIndex, (String)(CollUtil.isEmpty(sheetNames) ? "sheet" + itemIndex + "1" : sheetNames.get(itemIndex))).head(headData.getClass())).build();
                writer.write(list, sheet);
            }
        }
        catch (Exception e) {
            LogUtils.error((String)"ExcelExportUtil exportFile in error:{}", (Object[])new Object[]{e});
        }
        finally {
            if (null != writer) {
                writer.finish();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <T> void exportWithDynamicData(String fileName, List<List<List<String>>> head, List<List<List<T>>> exportData, List<String> sheetNames, HttpServletResponse response) throws IOException {
        ExcelWriter writer = null;
        try {
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            HorizontalCellStyleStrategy horizontalCellStyleStrategy = ExcelExportUtil.getExportDefaultStyle();
            SimpleColumnWidthStyleStrategy columnWidthStyleStrategy = new SimpleColumnWidthStyleStrategy(ExportConstant.DEFAULT_CELL_LENGTH);
            writer = ((ExcelWriterBuilder)((ExcelWriterBuilder)EasyExcel.write((OutputStream)response.getOutputStream()).registerWriteHandler((WriteHandler)horizontalCellStyleStrategy)).registerWriteHandler((WriteHandler)columnWidthStyleStrategy)).build();
            for (int i = 0; i < exportData.size(); ++i) {
                List<List<T>> tableData = exportData.get(i);
                WriteSheet sheet = ((ExcelWriterSheetBuilder)EasyExcel.writerSheet((Integer)i, (String)(CollUtil.isEmpty(sheetNames) ? "sheet" + i + "1" : sheetNames.get(i))).head(head.get(i))).build();
                writer.write(tableData, sheet);
            }
        }
        finally {
            if (Objects.nonNull(writer)) {
                writer.finish();
            }
        }
    }

    public static void downloadTemplate(String filePath, String fileName, String fileSuffix, HttpServletResponse response) {
        try {
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + fileSuffix);
            byte[] bytes = ResourceUtil.readBytes((String)filePath);
            response.getOutputStream().write(bytes);
        }
        catch (Exception e) {
            LogUtils.error((String)"ExcelExportUtil downloadTemplate in error:{}", (Object[])new Object[]{e});
        }
    }

    private static HorizontalCellStyleStrategy getExportDefaultStyle() {
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setBold(Boolean.valueOf(true));
        headWriteCellStyle.setWriteFont(headWriteFont);
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        HorizontalCellStyleStrategy horizontalCellStyleStrategy = new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
        return horizontalCellStyleStrategy;
    }
}

