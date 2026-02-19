/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  com.univocity.parsers.common.processor.BeanWriterProcessor
 *  com.univocity.parsers.common.processor.RowWriterProcessor
 *  com.univocity.parsers.csv.CsvWriter
 *  com.univocity.parsers.csv.CsvWriterSettings
 *  jakarta.servlet.http.HttpServletResponse
 */
package com.kuma.boot.office.csv;

import com.kuma.boot.common.utils.log.LogUtils;
import com.univocity.parsers.common.processor.BeanWriterProcessor;
import com.univocity.parsers.common.processor.RowWriterProcessor;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
import jakarta.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

public class CsvExportUtil<T> {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <T> void exportCsvWithString(HttpServletResponse response, String fileName, List<T> head, List<List<T>> rowDataList) {
        CsvWriter writer = null;
        try {
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".csv");
            CsvWriterSettings setting = CsvExportUtil.getDefaultWriteSetting();
            writer = new CsvWriter((OutputStream)response.getOutputStream(), setting);
            writer.writeHeaders(head);
            writer.writeStringRows(rowDataList);
            writer.flush();
        }
        catch (Exception e) {
            LogUtils.error((String)"CsvExportUtil exportCsv in error:{}", (Object[])new Object[]{e});
        }
        finally {
            if (Objects.nonNull(writer)) {
                writer.close();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <T> void exportCsvWithBean(HttpServletResponse response, String fileName, T head, List<T> rowDataList) {
        CsvWriter writer = null;
        try {
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".csv");
            CsvWriterSettings setting = CsvExportUtil.getDefaultWriteSetting();
            BeanWriterProcessor beanWriter = new BeanWriterProcessor(head.getClass());
            setting.setRowWriterProcessor((RowWriterProcessor)beanWriter);
            writer = new CsvWriter((OutputStream)response.getOutputStream(), setting);
            writer.processRecords(rowDataList);
            writer.flush();
        }
        catch (Exception e) {
            LogUtils.error((String)"CsvExportUtil exportCsvWithBean in error:{}", (Object[])new Object[]{e});
        }
        finally {
            if (Objects.nonNull(writer)) {
                writer.close();
            }
        }
    }

    private static CsvWriterSettings getDefaultWriteSetting() {
        CsvWriterSettings settings = new CsvWriterSettings();
        settings.setNullValue("");
        settings.setHeaderWritingEnabled(Boolean.TRUE.booleanValue());
        return settings;
    }
}

