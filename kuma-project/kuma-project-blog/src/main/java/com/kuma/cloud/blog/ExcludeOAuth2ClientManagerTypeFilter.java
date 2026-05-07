/*
 * Blog uses JWT resource-server only for native/JVM startup; Gradle excludes the OAuth2 Client starter,
 * which removes ClientRegistration from the classpath so Spring Security's OAuth2ImportSelector does not
 * import OAuth2ClientConfiguration (avoids early MapperFactoryBean + AOT Class<?> injection failure).
 *
 * This filter uses ASM MetadataReader only (no class loading) so the excluded configuration class name
 * is never resolved against missing OAuth2 client types on the classpath.
 */
package com.kuma.cloud.blog;

import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

final class ExcludeOAuth2ClientManagerTypeFilter implements TypeFilter {

    private static final String OAUTH2_CLIENT_MANAGER_CONFIGURATION =
            "com.kuma.boot.security.spring.autoconfigure.OAuth2ClientManagerConfiguration";

    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) {
        return OAUTH2_CLIENT_MANAGER_CONFIGURATION.equals(metadataReader.getClassMetadata().getClassName());
    }
}
