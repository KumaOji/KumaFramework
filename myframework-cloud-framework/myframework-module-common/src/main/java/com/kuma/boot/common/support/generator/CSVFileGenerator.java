/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * CSVFileGenerator
 * @author kuma
 * @version 2023.12
 * @since 2023-12-18 15:09:22
 */
/**
 * CSVFileGenerator
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public class CSVFileGenerator {

    private static final String LINE_SEPERATOR = System.lineSeparator();

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    public static void generate(
            List<HashMap<String, Object>> data, String[] columns, String fileName ) {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }

        for (Map<String, Object> objects : data) {
            List<String> result = Lists.newArrayList();
            for (String column : columns) {
                if (objects.get(column) != null) {
                    result.add(objects.get(column).toString());
                } else {
                    result.add("");
                }
            }

            String lineData = Joiner.on(",").skipNulls().join(result);
            try {
                Files.asCharSink(file, CHARSET, FileWriteMode.APPEND)
                        .write(lineData + LINE_SEPERATOR);
            } catch (IOException e) {
                LogUtils.error(e);
            }
        }
    }
}
