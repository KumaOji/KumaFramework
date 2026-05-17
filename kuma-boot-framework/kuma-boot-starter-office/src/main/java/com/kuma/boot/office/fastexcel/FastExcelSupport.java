/*
 * Copyright 2021-2024 spring-boot-extension the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.office.fastexcel;

import cn.idev.excel.ExcelReader;
import cn.idev.excel.ExcelWriter;
import cn.idev.excel.FastExcel;
import cn.idev.excel.read.metadata.ReadSheet;
import cn.idev.excel.write.builder.ExcelWriterBuilder;
import cn.idev.excel.write.handler.SheetWriteHandler;
import cn.idev.excel.write.metadata.WriteSheet;
import cn.idev.excel.write.metadata.holder.WriteSheetHolder;
import cn.idev.excel.write.metadata.holder.WriteWorkbookHolder;
import com.kuma.boot.core.utils.io.ResourceUtils;
import com.kuma.boot.common.utils.lambda.StreamUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.office.fastexcel.annotation.ResponseExcel;
import com.kuma.boot.office.fastexcel.listener.ExcelMapReadListener;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * The type Easy excel utils.
 */
public class FastExcelSupport {

    /**
     * Read.
     * @param in the in
     * @param excelModelClass the excel model class
     * @param listener the listener
     * @param ignoreEmptyRow the ignore empty row
     */
    public static void read(InputStream in, Class<?> excelModelClass, ExcelMapReadListener<?> listener,
                            Boolean ignoreEmptyRow) {
        try (ExcelReader excelReader = FastExcel.read(in, listener).ignoreEmptyRow(ignoreEmptyRow).build()) {
            List<ReadSheet> readSheets = excelReader.excelExecutor()
                    .sheetList()
                    .stream()
                    .map(sheet -> FastExcel.readSheet(sheet.getSheetNo(), sheet.getSheetName())
                            .head(excelModelClass)
                            .build())
                    .toList();
            excelReader.read(readSheets);
            excelReader.finish();
        }
    }

    /**
     * Write.
     * @param outputStream the output stream
     * @param excelModelClass the excel model class
     * @param location the location
     * @param result the result
     */
    public static void write(OutputStream outputStream, Class<?> excelModelClass, String location,
                             Map<String, ? extends Collection<?>> result) {
        ExcelWriterBuilder builder = FastExcel.write(outputStream);
        if (StringUtils.hasText(location)) {
            try {
                File file = ResourceUtils.getFile(location);
                builder.withTemplate(file);
                templateWrite(builder, result);
                return;
            }
            catch (FileNotFoundException e) {
                LogUtils.info("FastExcel使用模板错误:{}", e.getMessage(), e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        builder.head(excelModelClass);
        ordinaryWrite(FastExcel.write(outputStream, excelModelClass), result);
    }

    private static void ordinaryWrite(ExcelWriterBuilder builder, Map<String, ? extends Collection<?>> result) {
        try (ExcelWriter writer = builder.build()) {
            for (Map.Entry<String, ? extends Collection<?>> entry : result.entrySet()) {
                WriteSheet sheet = FastExcel.writerSheet(entry.getKey()).build();
                writer.write(entry.getValue(), sheet);
            }
            writer.finish();
        }
    }

    private static void templateWrite(ExcelWriterBuilder builder, Map<String, ? extends Collection<?>> result) {
        try (ExcelWriter writer = builder.build()) {
            result.entrySet().forEach(StreamUtils.forEachWithIndex(0, (entry, index) -> {
                WriteSheet writeSheet = FastExcel.writerSheet(index, entry.getKey())
                        .registerWriteHandler(new SheetWriteHandler() {
                            @Override
                            public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder,
                                                         WriteSheetHolder writeSheetHolder) {
                                writeWorkbookHolder.getCachedWorkbook().setSheetName(index, entry.getKey());
                            }
                        })
                        .build();
                writer.write(entry.getValue(), writeSheet);
            }));
            writer.finish();
        }
    }

    /**
     * File name string.
     * @param excelReturn the excel return
     * @return the string
     */
    public static String fileName(ResponseExcel excelReturn) {
        String template = excelReturn.template();
        String suffix;
        if (StringUtils.hasText(template)) {
            int index = template.lastIndexOf('.');
            suffix = template.substring(index);
        }
        else {
            suffix = excelReturn.suffix().getName();
        }
        return excelReturn.fileName().concat(suffix);
    }

}
