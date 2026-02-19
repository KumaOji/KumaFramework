/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.io.FileUtil
 *  com.kuma.boot.common.utils.log.LogUtils
 */
package com.kuma.boot.office.convert.util;

import cn.hutool.core.io.FileUtil;
import com.kuma.boot.common.utils.log.LogUtils;
import java.io.IOException;

public class WkHtmlUtil {
    private static final String TOOL_WIN_ROOT_DIRECTORY = "D:/zhengqingya/soft/soft-dev/wkhtmltopdf/bin/";

    public static byte[] html2ImageBytes(String cmd, String sourceFilePath, String targetFilePath) throws Exception {
        return WkHtmlUtil.baseTool("wkhtmltoimage", cmd, sourceFilePath, targetFilePath);
    }

    public static byte[] html2PdfBytes(String cmd, String sourceFilePath, String targetFilePath) throws Exception {
        return WkHtmlUtil.baseTool("wkhtmltopdf", cmd, sourceFilePath, targetFilePath);
    }

    private static byte[] baseTool(String tool, String cmd, String sourceFilePath, String targetFilePath) throws Exception {
        FileUtil.mkParentDirs((String)targetFilePath);
        String command = String.format("%s %s %s %s", WkHtmlUtil.getToolRootPath() + tool, cmd, sourceFilePath, targetFilePath);
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command, null, null);
        }
        catch (IOException e) {
            throw new Exception("\u5de5\u5177\u4e22\u5931\uff0c\u8bf7\u8054\u7cfb\u7cfb\u7edf\u7ba1\u7406\u5458\uff01");
        }
        process.waitFor();
        LogUtils.info((String)"=============== FINISH: [{}] ===============", (Object[])new Object[]{command});
        return FileUtil.readBytes((String)targetFilePath);
    }

    private static String getToolRootPath() {
        String system = System.getProperty("os.name");
        if (system.contains("Windows")) {
            return TOOL_WIN_ROOT_DIRECTORY;
        }
        if (system.contains("Linux") || system.contains("Mac OS X")) {
            return "";
        }
        return "";
    }
}

