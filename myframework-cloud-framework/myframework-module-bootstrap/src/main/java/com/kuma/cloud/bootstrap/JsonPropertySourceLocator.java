/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.boot.json.JsonParser
 *  org.springframework.boot.json.JsonParserFactory
 *  org.springframework.cloud.bootstrap.config.PropertySourceLocator
 *  org.springframework.core.annotation.Order
 *  org.springframework.core.env.Environment
 *  org.springframework.core.env.MapPropertySource
 *  org.springframework.core.env.PropertySource
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

@Order(value=0)
public class JsonPropertySourceLocator
implements PropertySourceLocator {
    public PropertySource<?> locate(Environment environment) {
        return new KmcMapPropertySource("KmcMapPropertySource", this.mapPropertySource());
    }

    private Map<String, Object> mapPropertySource() {
        HashMap<String, Object> result = new HashMap<String, Object>();
        JsonParser parser = JsonParserFactory.getJsonParser();
        Map fileMap = parser.parseMap(this.readFile());
        this.processNestMap("", result, fileMap);
        return result;
    }

    private String readFile() {
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get("src/main/resources/kmc.json", new String[0]), StandardCharsets.UTF_8);
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

    private void processNestMap(String prefix, Map<String, Object> result, Map<String, Object> fileMap) {
        if (!((String)prefix).isEmpty()) {
            prefix = (String)prefix + ".";
        }
        for (Map.Entry<String, Object> entrySet : fileMap.entrySet()) {
            if (entrySet.getValue() instanceof Map) {
                this.processNestMap((String)prefix + entrySet.getKey(), result, (Map)entrySet.getValue());
                continue;
            }
            result.put((String)prefix + entrySet.getKey(), entrySet.getValue());
        }
    }

    public static class KmcMapPropertySource
    extends MapPropertySource {
        public KmcMapPropertySource(String name, Map<String, Object> source) {
            super(name, source);
        }
    }
}

