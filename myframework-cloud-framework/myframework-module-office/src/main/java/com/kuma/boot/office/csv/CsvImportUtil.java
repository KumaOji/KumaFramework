/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.univocity.parsers.common.DataProcessingException
 *  com.univocity.parsers.common.ParsingContext
 *  com.univocity.parsers.common.ProcessorErrorHandler
 *  com.univocity.parsers.common.RetryableErrorHandler
 *  com.univocity.parsers.common.processor.BeanListProcessor
 *  com.univocity.parsers.common.processor.RowListProcessor
 *  com.univocity.parsers.common.processor.core.Processor
 *  com.univocity.parsers.csv.CsvFormat
 *  com.univocity.parsers.csv.CsvParser
 *  com.univocity.parsers.csv.CsvParserSettings
 */
package com.kuma.boot.office.csv;

import com.kuma.boot.office.easyexcel.easyexcelimport.refactor.ThrowingConsumer;
import com.kuma.boot.office.easyexcel.easyexcelimport.valid.ImportValid;
import com.univocity.parsers.common.DataProcessingException;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.ProcessorErrorHandler;
import com.univocity.parsers.common.RetryableErrorHandler;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.common.processor.RowListProcessor;
import com.univocity.parsers.common.processor.core.Processor;
import com.univocity.parsers.csv.CsvFormat;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import java.io.InputStream;
import java.util.List;

public class CsvImportUtil<T> {
    public static <T> void importCsvWithString(InputStream inputStream, List<String> errorList, Class<T> rowDto, ThrowingConsumer<List<String[]>> rowAction) {
        RowListProcessor rowListProcessor = new RowListProcessor();
        CsvParserSettings setting = CsvImportUtil.getDefaultSetting(errorList);
        setting.setProcessor((Processor)rowListProcessor);
        CsvParser csvParser = new CsvParser(setting);
        csvParser.parse(inputStream);
        List rowDataList = rowListProcessor.getRows();
        CsvImportUtil.persistentStringDataToDb(rowDataList, rowAction);
    }

    private static <T> void persistentStringDataToDb(List<String[]> data, ThrowingConsumer<List<String[]>> persistentActionMethod) {
        Iterable dataList = null;
        dataList.forEach(persistentActionMethod);
    }

    public static <T> void importCsvWithBean(InputStream inputStream, List<String> errorList, Class<T> rowDtoClass, ThrowingConsumer<List<T>> rowAction) {
        BeanListProcessor rowProcessor = new BeanListProcessor(rowDtoClass);
        CsvParserSettings setting = CsvImportUtil.getDefaultSetting(errorList);
        setting.setProcessor((Processor)rowProcessor);
        CsvParser csvParser = new CsvParser(setting);
        csvParser.parse(inputStream);
        List dataList = rowProcessor.getBeans();
        for (Object row : dataList) {
            ImportValid.validRequireField(row, errorList);
        }
        CsvImportUtil.persistentBeanDataToDb(dataList, rowAction);
    }

    private static <T> void persistentBeanDataToDb(List<T> data, ThrowingConsumer<List<T>> persistentActionMethod) {
        Iterable dataList = null;
        dataList.forEach(persistentActionMethod);
    }

    private static CsvParserSettings getDefaultSetting(final List<String> errorList) {
        CsvParserSettings settings = new CsvParserSettings();
        ((CsvFormat)settings.getFormat()).setLineSeparator("\n");
        settings.setLineSeparatorDetectionEnabled(Boolean.TRUE.booleanValue());
        settings.setHeaderExtractionEnabled(true);
        settings.setProcessorErrorHandler((ProcessorErrorHandler)new RetryableErrorHandler<ParsingContext>(){

            public void handleError(DataProcessingException error, Object[] inputRow, ParsingContext context) {
                String errorLog = "row error details: column '" + error.getColumnName() + "' (index " + error.getColumnIndex() + ") has value '" + String.valueOf(inputRow[error.getColumnIndex()]) + " transfer error";
                errorList.add(errorLog);
            }
        });
        return settings;
    }
}

