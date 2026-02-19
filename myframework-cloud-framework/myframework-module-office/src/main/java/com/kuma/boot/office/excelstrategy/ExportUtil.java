/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  jakarta.servlet.ServletOutputStream
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  org.apache.poi.poifs.filesystem.DirectoryNode
 *  org.apache.poi.poifs.filesystem.DocumentEntry
 *  org.apache.poi.poifs.filesystem.POIFSFileSystem
 */
package com.kuma.boot.office.excelstrategy;

import com.kuma.boot.common.utils.log.LogUtils;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class ExportUtil {
    public static void write(String content) {
        File file = null;
        FileWriter fw = null;
        try {
            file = new File("D:\\lll.html");
            if (!file.exists()) {
                file.createNewFile();
            }
            fw = new FileWriter(file);
            fw.write(content);
            fw.flush();
            fw.close();
        }
        catch (IOException e) {
            LogUtils.error((Throwable)e);
        }
    }

    public static String txt2String(File file) {
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            Object s = null;
            String regex = "^\\d*\\.";
            Pattern p = Pattern.compile(regex);
            while ((s = br.readLine()) != null) {
                Matcher m = p.matcher((CharSequence)s);
                if (m.find()) {
                    s = "<h1 style=\"mso-outline-level:1;\"><b><span style=\"mso-spacerun:'yes';font-family:\u5b8b\u4f53;mso-ascii-font-family:Calibri;\n                    mso-hansi-font-family:Calibri;mso-bidi-font-family:'Times New Roman';font-weight:bold;\n                    font-size:10.0000pt;mso-font-kerning:22.0000pt;\"><font face=\"\u5b8b\u4f53\">" + (String)s + "</font></span></b><b><span style=\"mso-spacerun:'yes';font-family:\u5b8b\u4f53;mso-ascii-font-family:Calibri;\n                    mso-hansi-font-family:Calibri;mso-bidi-font-family:'Times New Roman';font-weight:bold;\n                    font-size:10.0000pt;mso-font-kerning:22.0000pt;\"><o:p></o:p></span></b></h1>";
                }
                result.append(System.lineSeparator() + (String)s);
            }
            br.close();
        }
        catch (Exception e) {
            LogUtils.error((Throwable)e);
        }
        return result.toString();
    }

    public void exportWord(HttpServletRequest request, HttpServletResponse response, String content) {
        try {
            byte[] b = content.getBytes("GB2312");
            ByteArrayInputStream bais = new ByteArrayInputStream(b);
            POIFSFileSystem poifs = new POIFSFileSystem();
            DirectoryNode directory = poifs.getRoot();
            DocumentEntry documentEntry = directory.createDocument("WordDocument", (InputStream)bais);
            String fileName = "wordFileName";
            request.setCharacterEncoding("GB2312");
            response.setContentType("application/msword");
            response.setHeader("Content-disposition", "attachment; filename=\u4e34\u5e8a\u79d1\u5ba4N2\u9898\u5e93.doc");
            ServletOutputStream ostream = response.getOutputStream();
            poifs.writeFilesystem((OutputStream)ostream);
            bais.close();
            ostream.close();
        }
        catch (Exception e) {
            System.out.println("\u5bfc\u51fa\u51fa\u9519\uff1a%s" + e.getMessage());
        }
    }
}

