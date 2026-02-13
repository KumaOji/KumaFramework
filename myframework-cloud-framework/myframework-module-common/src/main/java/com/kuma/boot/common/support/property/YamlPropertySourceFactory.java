/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.YamlPropertiesFactoryBean
 *  org.springframework.core.env.PropertiesPropertySource
 *  org.springframework.core.env.PropertySource
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.support.EncodedResource
 *  org.springframework.core.io.support.PropertySourceFactory
 */
package com.kuma.boot.common.support.property;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

public class YamlPropertySourceFactory
implements PropertySourceFactory {
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        Properties propertiesFromYaml = this.loadYamlIntoProperties(resource);
        String sourceName = name != null ? name : resource.getResource().getFilename();
        return new PropertiesPropertySource(sourceName, propertiesFromYaml);
    }

    private Properties loadYamlIntoProperties(EncodedResource resource) throws FileNotFoundException {
        try {
            YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
            factory.setResources(new Resource[]{resource.getResource()});
            factory.afterPropertiesSet();
            return factory.getObject();
        }
        catch (IllegalStateException e) {
            Throwable cause = e.getCause();
            if (cause instanceof FileNotFoundException) {
                throw (FileNotFoundException)e.getCause();
            }
            throw e;
        }
    }
}

