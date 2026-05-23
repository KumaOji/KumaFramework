package com.kuma.boot.common.banner;


/**
 * VersionBanner
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class VersionBanner extends AbstractBanner {

    public VersionBanner( Class<?> resourceClass, String resourceLocation, String defaultBanner ) {
        super(resourceClass, resourceLocation, defaultBanner);

        initialize();
    }

    @Override
    protected String generateBanner( String bannerText ) {
        if (bannerText == null) {
            String implementationVersion = resourceClass.getPackage().getImplementationVersion();
            if (implementationVersion != null) {
                return implementationVersion;
            } else {
                return defaultBanner;
            }
        } else {
            return bannerText;
        }
    }
}
