/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.cloud.bootstrap;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

/**
 * 获取本地json文件存储到 environment中
 */
@Order(0)
public class JsonPropertySourceLocator implements PropertySourceLocator {

    @Override
    public PropertySource<?> locate(Environment environment) {
        return new KmcMapPropertySource("KmcMapPropertySource", mapPropertySource());
    }

    private Map<String, Object> mapPropertySource() {
        Map<String, Object> result = new HashMap<>();
        JsonParser parser = JsonParserFactory.getJsonParser();
        Map<String, Object> fileMap = parser.parseMap(readFile());
        processNestMap("", result, fileMap);
        return result;
    }

    /**
     * 读取配置文件 kmc.json
     */
    private String readFile() {
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get("src/main/resources/kmc.json"),
                    StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line);
        }
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private void processNestMap(String prefix, Map<String, Object> result,
                                Map<String, Object> fileMap) {
        if (!prefix.isEmpty()) {
            prefix += ".";
        }
        for (Map.Entry<String, Object> entrySet : fileMap.entrySet()) {
            if (entrySet.getValue() instanceof Map) {
                processNestMap(prefix + entrySet.getKey(), result,
                        (Map<String, Object>) entrySet.getValue());
            }
            else {
                result.put(prefix + entrySet.getKey(), entrySet.getValue());
            }
        }
    }

    public static class KmcMapPropertySource extends MapPropertySource {

        /**
         * Create a new {@code MapPropertySource} with the given name and {@code Map}.
         *
         * @param name   the associated name
         * @param source the Map source (without {@code null} values in order to get consistent
         *               {@link #getProperty} and {@link #containsProperty} behavior)
         */
        public KmcMapPropertySource(String name, Map<String, Object> source) {
            super(name, source);
        }
    }
}
