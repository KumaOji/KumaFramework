/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.collect.Lists
 *  com.google.common.io.FileWriteMode
 *  com.google.common.io.Files
 */
package com.kuma.boot.common.support.generator;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import com.kuma.boot.common.utils.log.LogUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVFileGenerator {
    private static final String LINE_SEPERATOR = System.lineSeparator();
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    public static void generate(List<HashMap<String, Object>> data, String[] columns, String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
        for (Map map : data) {
            ArrayList result = Lists.newArrayList();
            for (String column : columns) {
                if (map.get(column) != null) {
                    result.add(map.get(column).toString());
                    continue;
                }
                result.add("");
            }
            String lineData = Joiner.on((String)",").skipNulls().join((Iterable)result);
            try {
                Files.asCharSink((File)file, (Charset)CHARSET, (FileWriteMode[])new FileWriteMode[]{FileWriteMode.APPEND}).write((CharSequence)(lineData + LINE_SEPERATOR));
            }
            catch (IOException e) {
                LogUtils.error(e);
            }
        }
    }
}

