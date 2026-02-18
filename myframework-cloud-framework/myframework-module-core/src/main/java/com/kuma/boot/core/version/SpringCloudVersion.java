/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.core.version;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.CodeSource;
import java.util.jar.Attributes.Name;
import java.util.jar.JarFile;
//import org.springframework.cloud.bootstrap.BootstrapConfiguration;

/**
 * Class that exposes the Spring Boot version. Fetches the {@link Name#IMPLEMENTATION_VERSION
 * Implementation-Version} manifest attribute from the jar file via {@link
 * Package#getImplementationVersion()}, falling back to locating the jar file that contains this
 * class and reading the {@code Implementation-Version} attribute from its manifest.
 *
 * <p>This class might not be able to determine the Spring Boot version in all environments.
 * Consider using a reflection-based check instead: For example, checking for the presence of a
 * specific Spring Boot method that you intend to call.
 *
 * @author Drummond Dawson
 * @author Hendrig Sellik
 * @author Andy Wilkinson
 * @since 1.3.0
 */
public final class SpringCloudVersion {

    private SpringCloudVersion() {}

    /**
     * Return the full version string of the present Spring Boot codebase, or {@code null} if it
     * cannot be determined.
     *
     * @return the version of Spring Boot or {@code null}
     * @see Package#getImplementationVersion()
     */
    public static String getVersion() {
        return determineSpringCloudVersion();
    }

    private static String determineSpringCloudVersion() {
        try {
            Class<?> bootstrapClass = Class.forName("org.springframework.cloud.bootstrap.BootstrapConfiguration");
            String implementationVersion = bootstrapClass.getPackage().getImplementationVersion();
            if (implementationVersion != null) {
                return implementationVersion;
            }
            CodeSource codeSource =
                    bootstrapClass.getProtectionDomain().getCodeSource();
            if (codeSource == null) {
                return null;
            }
            URL codeSourceLocation = codeSource.getLocation();

            URLConnection connection = codeSourceLocation.openConnection();
            if (connection instanceof JarURLConnection) {
                return getImplementationVersion(((JarURLConnection) connection).getJarFile());
            }
            try (JarFile jarFile = new JarFile(new File(codeSourceLocation.toURI()))) {
                return getImplementationVersion(jarFile);
            }
        } catch (Exception ex) {
            return "NOT IN SPRING CLOUD";
        }
    }

    private static String getImplementationVersion(JarFile jarFile) throws IOException {
        return jarFile.getManifest().getMainAttributes().getValue(Name.IMPLEMENTATION_VERSION);
    }
}
