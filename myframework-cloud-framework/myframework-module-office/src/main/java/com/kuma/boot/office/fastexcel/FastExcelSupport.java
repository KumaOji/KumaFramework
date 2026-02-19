/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.idev.excel.ExcelReader
 *  cn.idev.excel.ExcelWriter
 *  cn.idev.excel.FastExcel
 *  cn.idev.excel.read.builder.ExcelReaderSheetBuilder
 *  cn.idev.excel.read.metadata.ReadSheet
 *  cn.idev.excel.write.builder.ExcelWriterBuilder
 *  cn.idev.excel.write.builder.ExcelWriterSheetBuilder
 *  cn.idev.excel.write.handler.SheetWriteHandler
 *  cn.idev.excel.write.handler.WriteHandler
 *  cn.idev.excel.write.metadata.WriteSheet
 *  cn.idev.excel.write.metadata.holder.WriteSheetHolder
 *  cn.idev.excel.write.metadata.holder.WriteWorkbookHolder
 *  com.kuma.boot.common.utils.io.ResourceUtils
 *  com.kuma.boot.common.utils.lambda.StreamUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.util.StringUtils
 */
package com.kuma.boot.office.fastexcel;

import cn.idev.excel.ExcelReader;
import cn.idev.excel.ExcelWriter;
import cn.idev.excel.FastExcel;
import cn.idev.excel.read.builder.ExcelReaderSheetBuilder;
import cn.idev.excel.read.metadata.ReadSheet;
import cn.idev.excel.write.builder.ExcelWriterBuilder;
import cn.idev.excel.write.builder.ExcelWriterSheetBuilder;
import cn.idev.excel.write.handler.SheetWriteHandler;
import cn.idev.excel.write.handler.WriteHandler;
import cn.idev.excel.write.metadata.WriteSheet;
import cn.idev.excel.write.metadata.holder.WriteSheetHolder;
import cn.idev.excel.write.metadata.holder.WriteWorkbookHolder;
import com.kuma.boot.common.utils.io.ResourceUtils;
import com.kuma.boot.common.utils.lambda.StreamUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.office.fastexcel.annotation.ResponseExcel;
import com.kuma.boot.office.fastexcel.listener.ExcelMapReadListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.util.StringUtils;

public class FastExcelSupport {
    public static void read(InputStream in, Class<?> excelModelClass, ExcelMapReadListener<?> listener, Boolean ignoreEmptyRow) {
        try (ExcelReader excelReader = FastExcel.read((InputStream)in, listener).ignoreEmptyRow(ignoreEmptyRow).build();){
            List<ReadSheet> readSheets = excelReader.excelExecutor().sheetList().stream().map(sheet -> ((ExcelReaderSheetBuilder)FastExcel.readSheet((Integer)sheet.getSheetNo(), (String)sheet.getSheetName()).head(excelModelClass)).build()).toList();
            excelReader.read(readSheets);
            excelReader.finish();
        }
    }

    public static void write(OutputStream outputStream, Class<?> excelModelClass, String location, Map<String, ? extends Collection<?>> result) {
        ExcelWriterBuilder builder = FastExcel.write((OutputStream)outputStream);
        if (StringUtils.hasText((String)location)) {
            try {
                File file = ResourceUtils.getFile((String)location);
                builder.withTemplate(file);
                FastExcelSupport.templateWrite(builder, result);
                return;
            }
            catch (FileNotFoundException e) {
                LogUtils.info((String)"FastExcel\u4f7f\u7528\u6a21\u677f\u9519\u8bef:{}", (Object[])new Object[]{e.getMessage(), e});
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        builder.head(excelModelClass);
        FastExcelSupport.ordinaryWrite(FastExcel.write((OutputStream)outputStream, excelModelClass), result);
    }

    private static void ordinaryWrite(ExcelWriterBuilder builder, Map<String, ? extends Collection<?>> result) {
        try (ExcelWriter writer = builder.build();){
            for (Map.Entry<String, Collection<?>> entry : result.entrySet()) {
                WriteSheet sheet = FastExcel.writerSheet((String)entry.getKey()).build();
                writer.write(entry.getValue(), sheet);
            }
            writer.finish();
        }
    }

    private static void templateWrite(ExcelWriterBuilder builder, Map<String, ? extends Collection<?>> result) {
        try (ExcelWriter writer = builder.build();){
            result.entrySet().forEach(StreamUtils.forEachWithIndex((int)0, (entry, index) -> {
                WriteSheet writeSheet = ((ExcelWriterSheetBuilder)FastExcel.writerSheet((Integer)index, (String)((String)entry.getKey())).registerWriteHandler((WriteHandler)new SheetWriteHandler(){
                    final /* synthetic */ Integer val$index;
                    final /* synthetic */ Map.Entry val$entry;
                    {
                        this.val$index = n;
                        this.val$entry = entry;
                    }

                    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
                        writeWorkbookHolder.getCachedWorkbook().setSheetName(this.val$index.intValue(), (String)this.val$entry.getKey());
                    }
                })).build();
                writer.write((Collection)entry.getValue(), writeSheet);
            }));
            writer.finish();
        }
    }

    public static String fileName(ResponseExcel excelReturn) {
        String suffix;
        String template = excelReturn.template();
        if (StringUtils.hasText((String)template)) {
            int index = template.lastIndexOf(46);
            suffix = template.substring(index);
        } else {
            suffix = excelReturn.suffix().getName();
        }
        return excelReturn.fileName().concat(suffix);
    }
}

