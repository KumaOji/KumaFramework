/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.aspose.cells.PdfSaveOptions
 *  com.aspose.cells.SaveOptions
 *  com.aspose.cells.Workbook
 */
package com.kuma.boot.office.convert.excel2pdf;

import com.aspose.cells.PdfSaveOptions;
import com.aspose.cells.SaveOptions;
import com.aspose.cells.Workbook;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public class Excel2PdfUtil {
    public static byte[] excelBytes2PdfBytes(byte[] excelBytes) throws Exception {
        Workbook workbook = new Workbook((InputStream)new ByteArrayInputStream(excelBytes));
        PdfSaveOptions pso = new PdfSaveOptions();
        pso.setAllColumnsInOnePagePerSheet(true);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.save((OutputStream)outputStream, (SaveOptions)pso);
        return outputStream.toByteArray();
    }

    public static File excelBytes2PdfFile(byte[] excelBytes, String pdfFilePath) throws Exception {
        Workbook workbook = new Workbook((InputStream)new ByteArrayInputStream(excelBytes));
        PdfSaveOptions pso = new PdfSaveOptions();
        pso.setAllColumnsInOnePagePerSheet(true);
        workbook.save(pdfFilePath, (SaveOptions)pso);
        return new File(pdfFilePath);
    }
}

