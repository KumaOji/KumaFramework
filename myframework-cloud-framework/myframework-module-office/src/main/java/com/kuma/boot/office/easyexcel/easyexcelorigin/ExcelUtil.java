/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.alibaba.excel.EasyExcel
 *  com.alibaba.excel.ExcelWriter
 *  com.alibaba.excel.write.builder.ExcelWriterBuilder
 *  com.alibaba.excel.write.builder.ExcelWriterTableBuilder
 *  com.alibaba.excel.write.handler.WriteHandler
 *  com.alibaba.excel.write.metadata.WriteSheet
 *  com.alibaba.excel.write.metadata.WriteTable
 *  com.alibaba.excel.write.metadata.style.WriteCellStyle
 *  com.alibaba.excel.write.metadata.style.WriteFont
 *  com.alibaba.excel.write.style.HorizontalCellStyleStrategy
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  jakarta.servlet.http.HttpServletResponse
 *  org.apache.poi.ss.usermodel.HorizontalAlignment
 *  org.apache.poi.ss.usermodel.IndexedColors
 *  org.springframework.web.multipart.MultipartFile
 */
package com.kuma.boot.office.easyexcel.easyexcelorigin;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.builder.ExcelWriterTableBuilder;
import com.alibaba.excel.write.handler.WriteHandler;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteTable;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.office.easyexcel.easyexcelconvert.core.CommentWriteHandler;
import com.kuma.boot.office.easyexcel.easyexcelconvert.core.CustomSheetWriteHandler;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.web.multipart.MultipartFile;

public class ExcelUtil {
    public static void export(String filename, List<?> dataResult, Class<?> clazz, HttpServletResponse response) {
        OutputStream outputStream;
        block12: {
            response.setStatus(200);
            outputStream = null;
            ExcelWriter excelWriter = null;
            try {
                if (StringUtils.isBlank((String)filename)) {
                    throw new RuntimeException("'filename' \u4e0d\u80fd\u4e3a\u7a7a");
                }
                String fileName = filename.concat(".xlsx");
                response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
                outputStream = response.getOutputStream();
                excelWriter = dataResult == null ? ExcelUtil.getTemplateExcelWriter(outputStream) : ExcelUtil.getExportExcelWriter(outputStream);
                WriteTable writeTable = ((ExcelWriterTableBuilder)((ExcelWriterTableBuilder)EasyExcel.writerTable((Integer)0).head(clazz)).needHead(Boolean.valueOf(true))).build();
                WriteSheet writeSheet = EasyExcel.writerSheet((String)fileName).build();
                excelWriter.write(dataResult, writeSheet, writeTable);
                if (excelWriter == null) break block12;
            }
            catch (Exception e) {
                try {
                    LogUtils.error((String)"\u5bfc\u51faexcel\u6570\u636e\u5f02\u5e38\uff1a", (Object[])new Object[]{e});
                    throw new RuntimeException(e);
                }
                catch (Throwable throwable) {
                    if (excelWriter != null) {
                        excelWriter.finish();
                    }
                    if (outputStream != null) {
                        try {
                            outputStream.flush();
                            outputStream.close();
                        }
                        catch (IOException e2) {
                            LogUtils.error((String)"\u5bfc\u51fa\u6570\u636e\u5173\u95ed\u6d41\u5f02\u5e38", (Object[])new Object[]{e2});
                        }
                    }
                    throw throwable;
                }
            }
            excelWriter.finish();
        }
        if (outputStream != null) {
            try {
                outputStream.flush();
                outputStream.close();
            }
            catch (IOException e) {
                LogUtils.error((String)"\u5bfc\u51fa\u6570\u636e\u5173\u95ed\u6d41\u5f02\u5e38", (Object[])new Object[]{e});
            }
        }
    }

    private static ExcelWriter getTemplateExcelWriter(OutputStream outputStream) {
        return ((ExcelWriterBuilder)((ExcelWriterBuilder)((ExcelWriterBuilder)EasyExcel.write((OutputStream)outputStream).registerWriteHandler((WriteHandler)new CommentWriteHandler())).registerWriteHandler((WriteHandler)new CustomSheetWriteHandler())).registerWriteHandler((WriteHandler)ExcelUtil.getStyleStrategy())).build();
    }

    private static ExcelWriter getExportExcelWriter(OutputStream outputStream) {
        return ((ExcelWriterBuilder)EasyExcel.write((OutputStream)outputStream).registerWriteHandler((WriteHandler)ExcelUtil.getStyleStrategy())).build();
    }

    private static HorizontalCellStyleStrategy getStyleStrategy() {
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        headWriteCellStyle.setFillForegroundColor(Short.valueOf(IndexedColors.GREY_25_PERCENT.getIndex()));
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints(Short.valueOf((short)13));
        headWriteFont.setBold(Boolean.valueOf(true));
        headWriteCellStyle.setWriteFont(headWriteFont);
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        WriteCellStyle writeCellStyle = new WriteCellStyle();
        writeCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        return new HorizontalCellStyleStrategy(headWriteCellStyle, writeCellStyle);
    }

    public static List<?> importExcel(MultipartFile file, Class<?> clazz) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("\u6ca1\u6709\u6587\u4ef6\u6216\u8005\u6587\u4ef6\u5185\u5bb9\u4e3a\u7a7a\uff01");
        }
        List dataList = null;
        BufferedInputStream ipt = null;
        try {
            InputStream is = file.getInputStream();
            ipt = new BufferedInputStream(is);
            ExcelListener listener = new ExcelListener();
            EasyExcel.read((InputStream)ipt, clazz, listener).sheet().doRead();
            dataList = listener.getDataList();
        }
        catch (Exception e) {
            LogUtils.error((String)String.valueOf(e), (Object[])new Object[0]);
            throw new RuntimeException("\u6570\u636e\u5bfc\u5165\u5931\u8d25\uff01" + String.valueOf(e));
        }
        return dataList;
    }
}

