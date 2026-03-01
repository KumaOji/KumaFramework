/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.cloud.bootstrap.config.PropertySourceLocator
 *  org.springframework.core.annotation.Order
 *  org.springframework.core.env.CompositePropertySource
 *  org.springframework.core.env.Environment
 *  org.springframework.core.env.MapPropertySource
 *  org.springframework.core.env.PropertySource
 */
package com.kuma.cloud.bootstrap;

import java.util.Collection;
import java.util.HashMap;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

@Order(value=1993)
public class KmcBootstrapPropertySourceLocator
implements PropertySourceLocator {
    public PropertySource<?> locate(Environment environment) {
        HashMap<String, String> property = new HashMap<String, String>();
        CompositePropertySource composite = new CompositePropertySource("kmc");
        property.put("kmcProperty", "kmcProperty");
        composite.addPropertySource((PropertySource)new MapPropertySource("kmc", property));
        return composite;
    }

    public Collection<PropertySource<?>> locateCollection(Environment environment) {
        return super.locateCollection(environment);
    }
}

