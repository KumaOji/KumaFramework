package com.kuma.boot.core.banner;

import com.kuma.boot.core.utils.CoreUtils;

import java.io.PrintStream;
import java.util.List;

import org.springframework.boot.ResourceBanner;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.env.PropertySourcesPropertyResolver;
import org.springframework.core.io.Resource;

/**
 * KmcBanner
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class KmcBanner extends ResourceBanner {

    public KmcBanner( Resource resource ) {
        super(resource);
    }

    @Override
    public void printBanner( Environment environment, Class<?> sourceClass, PrintStream out ) {
        super.printBanner(environment, sourceClass, out);
    }

    @Override
    protected List<PropertyResolver> getPropertyResolvers( Environment environment,
                                                           Class<?> sourceClass ) {

        List<PropertyResolver> propertyResolvers = super.getPropertyResolvers(environment, sourceClass);

        propertyResolvers.add(
                new PropertySourcesPropertyResolver(createEmptyDefaultKmcSources(environment, sourceClass)));

        return propertyResolvers;
    }

    private MutablePropertySources createEmptyDefaultKmcSources( Environment environment, Class<?> sourceClass ) {
        MutablePropertySources emptyDefaultSources = new MutablePropertySources();
        emptyDefaultSources.addLast(CoreUtils.getVersionSource(sourceClass, environment));
        emptyDefaultSources.addLast(CoreUtils.getUrlSource());
        return emptyDefaultSources;
    }


}
