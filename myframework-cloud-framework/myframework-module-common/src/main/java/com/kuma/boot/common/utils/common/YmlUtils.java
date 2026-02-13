/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections4.MapUtils
 *  org.apache.commons.lang3.ObjectUtils
 *  org.yaml.snakeyaml.Yaml
 */
package com.kuma.boot.common.utils.common;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.yaml.snakeyaml.Yaml;

public class YmlUtils {
    private static final String BOOTSTRAP_YML = "bootstrap.yml";
    private static final String APPLICATION_YML = "application.yml";

    private YmlUtils() {
    }

    public static Object getBootstrapValue(String key) {
        return YmlUtils.getValueByYml(BOOTSTRAP_YML, key);
    }

    public static Object getApplicationValue(String key) {
        return YmlUtils.getValueByYml(APPLICATION_YML, key);
    }

    public static Object getValueByYml(String fileName, String key) {
        String[] keys;
        Map map = YmlUtils.getYml(fileName);
        if (MapUtils.isEmpty(map)) {
            return null;
        }
        Object result = null;
        for (String k : keys = key.split("\\.")) {
            Object o = map.get(k);
            if (ObjectUtils.isNotEmpty((Object)o)) {
                if (o instanceof Map && !k.equals(key) && !key.endsWith("." + k)) {
                    map = (Map)o;
                    continue;
                }
                result = o;
                continue;
            }
            result = null;
        }
        return result;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static Map<String, Object> getYml(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return null;
        }
        try (InputStream inputStream = YmlUtils.class.getClassLoader().getResourceAsStream(fileName);){
            if (ObjectUtils.isEmpty((Object)inputStream)) {
                Map<String, Object> map2 = null;
                return map2;
            }
            Yaml yaml = new Yaml();
            Map map = (Map)yaml.loadAs(inputStream, Map.class);
            return map;
        }
        catch (IOException e) {
            LogUtils.error(e, "IO\u6d41\u5904\u7406\u5931\u8d25", new Object[0]);
            return null;
        }
    }
}

