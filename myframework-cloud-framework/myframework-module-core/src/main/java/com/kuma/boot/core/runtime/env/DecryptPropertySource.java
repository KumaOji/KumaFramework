package com.kuma.boot.core.runtime.env;

import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.Map;

// 这里只继承了MapPropertySource.
/**
 * DecryptPropertySource
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class DecryptPropertySource extends MapPropertySource {

    private final MapPropertySource decryptPropertySource;

    /**
     * constractor.
     *
     * @param name source name
     * @param decryptPropertySource decryptPropertySource
     */
    public DecryptPropertySource( String name, MapPropertySource decryptPropertySource ) {
        super(name, decryptPropertySource.getSource());
        this.decryptPropertySource = decryptPropertySource;
    }

    /**
     * get property value by property name.
     *
     * @param name property name
     * @return property value
     */
    public Object getProperty( String name ) {
        Object value = decryptPropertySource.getProperty(name);
        if (value instanceof String) {
            // 在这里对你的属性值进行解密、处理等等,并返回处理后的属性值。
            //return ...;
            return value;
        }
        return value;
    }


    /**
     * get DecryptResource.
     *
     * @return DecryptResource
     */
    public PropertySource<Map<String, Object>> getDecryptResource() {
        return this.decryptPropertySource;
    }
}

