/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.spire.doc.Document
 *  com.spire.doc.FileFormat
 *  com.spire.pdf.FileFormat
 *  com.spire.pdf.PdfDocument
 *  com.spire.pdf.widget.PdfPageCollection
 *  com.kuma.boot.common.utils.log.LogUtils
 */
package com.kuma.boot.office.convert.pdf2word;

import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.spire.pdf.PdfDocument;
import com.spire.pdf.widget.PdfPageCollection;
import com.kuma.boot.common.utils.log.LogUtils;
import java.io.File;

public class Pdf2WordUtil {
    String splitPath = "./split/";
    String docPath = "./doc/";

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void pdf2Word(String srcPath, String desPath) {
        boolean result = false;
        try {
            boolean flag = this.isPDFFile(srcPath);
            boolean flag1 = this.create();
            if (flag && flag1) {
                PdfDocument pdf = new PdfDocument();
                pdf.loadFromFile(srcPath);
                PdfPageCollection num = pdf.getPages();
                if (num.getCount() <= 10) {
                    pdf.saveToFile(desPath, com.spire.pdf.FileFormat.DOCX);
                } else {
                    File[] fs;
                    pdf.split(this.splitPath + "test{0}.pdf", 0);
                    for (File f : fs = this.getSplitFiles(this.splitPath)) {
                        PdfDocument sonpdf = new PdfDocument();
                        sonpdf.loadFromFile(f.getAbsolutePath());
                        sonpdf.saveToFile(this.docPath + f.getName().substring(0, f.getName().length() - 4) + ".docx", com.spire.pdf.FileFormat.DOCX);
                    }
                    try {
                        result = this.merge(this.docPath, desPath);
                        LogUtils.debug((String)String.valueOf(result), (Object[])new Object[0]);
                    }
                    catch (Exception e) {
                        LogUtils.error((Throwable)e);
                    }
                }
            } else {
                LogUtils.debug((String)"\u8f93\u5165\u7684\u4e0d\u662fpdf\u6587\u4ef6", (Object[])new Object[0]);
            }
        }
        catch (Exception e) {
            LogUtils.error((Throwable)e);
        }
        finally {
            if (result) {
                this.clearFiles(this.splitPath);
                this.clearFiles(this.docPath);
            }
        }
        LogUtils.debug((String)"\u8f6c\u6362\u6210\u529f", (Object[])new Object[0]);
    }

    private boolean create() {
        File f = new File(this.splitPath);
        File f1 = new File(this.docPath);
        if (!f.exists()) {
            f.mkdirs();
        }
        if (!f.exists()) {
            f1.mkdirs();
        }
        return true;
    }

    private boolean isPDFFile(String srcPath2) {
        File file = new File(srcPath2);
        String filename = file.getName();
        return filename.endsWith(".pdf");
    }

    private File[] getSplitFiles(String path) {
        File f = new File(path);
        File[] fs = f.listFiles();
        if (fs == null) {
            return null;
        }
        return fs;
    }

    public void clearFiles(String workspaceRootPath) {
        File file = new File(workspaceRootPath);
        if (file.exists()) {
            this.deleteFile(file);
        }
    }

    public void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; ++i) {
                this.deleteFile(files[i]);
            }
        }
        file.delete();
    }

    private boolean merge(String docPath, String desPath) {
        File[] fs = this.getSplitFileList(docPath);
        LogUtils.info((String)docPath, (Object[])new Object[0]);
        Document document = new Document(docPath + "test0.docx");
        for (int i = 1; i < fs.length; ++i) {
            document.insertTextFromFile(docPath + "test" + i + ".docx", FileFormat.Docx_2013);
        }
        document.saveToFile(desPath);
        return true;
    }

    private File[] getSplitFileList(String path) {
        File f = new File(path);
        return f.listFiles();
    }
}

