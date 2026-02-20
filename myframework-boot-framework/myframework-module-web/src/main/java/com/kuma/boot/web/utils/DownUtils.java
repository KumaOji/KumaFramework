/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  com.kuma.boot.common.utils.servlet.RequestUtils
 *  jakarta.servlet.ServletOutputStream
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  org.apache.poi.ss.usermodel.Workbook
 */
package com.kuma.boot.web.utils;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.servlet.RequestUtils;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.imageio.ImageIO;
import org.apache.poi.ss.usermodel.Workbook;

public class DownUtils {
    public static void dowloadExcel(Workbook workbook, String fileName) {
        try {
            HttpServletResponse response = RequestUtils.getResponse();
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write((OutputStream)outputStream);
        }
        catch (IOException e) {
            LogUtils.error((Throwable)e);
        }
    }

    public static void dowloadFile(File file, String fileName) {
        HttpServletResponse response = RequestUtils.getResponse();
        HttpServletRequest request = RequestUtils.getRequest();
        try {
            int i;
            FileInputStream is = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/x-download");
            String codeFileName = "";
            String agent = request.getHeader("USER-AGENT").toLowerCase();
            codeFileName = agent.contains("msie") || agent.contains("trident") ? URLEncoder.encode(fileName, StandardCharsets.UTF_8) : (agent.contains("mozilla") ? new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) : URLEncoder.encode(fileName, StandardCharsets.UTF_8));
            response.setHeader("Content-Disposition", "attachment;filename=\"" + codeFileName + "\"");
            ServletOutputStream os = response.getOutputStream();
            byte[] buff = new byte[8192];
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
            }
            os.flush();
        }
        catch (Exception e) {
            LogUtils.error((Throwable)e);
        }
    }

    public static Boolean dowloadFile(String paths, String fileName) {
        HttpServletResponse response = RequestUtils.getResponse();
        HttpServletRequest request = RequestUtils.getRequest();
        try {
            int i;
            FileInputStream is = new FileInputStream(paths);
            BufferedInputStream bis = new BufferedInputStream(is);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain");
            if (fileName.contains(".svg")) {
                response.setContentType("image/svg+xml");
            }
            String codeFileName = "";
            String agent = request.getHeader("USER-AGENT").toLowerCase();
            codeFileName = agent.contains("msie") || agent.contains("trident") ? URLEncoder.encode(fileName, StandardCharsets.UTF_8) : (agent.contains("mozilla") ? new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) : URLEncoder.encode(fileName, StandardCharsets.UTF_8));
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(codeFileName.getBytes(), StandardCharsets.UTF_8));
            ServletOutputStream os = response.getOutputStream();
            byte[] buff = new byte[8192];
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
            }
            os.flush();
        }
        catch (Exception e) {
            LogUtils.error((Throwable)e);
            return false;
        }
        return true;
    }

    public static void write(BufferedImage image) {
        try {
            HttpServletResponse response = DownUtils.getResponse();
            ServletOutputStream outputStream = response.getOutputStream();
            ImageIO.write((RenderedImage)image, "PNG", (OutputStream)outputStream);
        }
        catch (Exception e) {
            LogUtils.error((Throwable)e);
        }
    }

    public static HttpServletResponse getResponse() {
        HttpServletResponse response = RequestUtils.getResponse();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expire", 0L);
        return response;
    }
}

