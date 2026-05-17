package com.kuma.boot.common.version;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.CodeSource;
import java.util.jar.Attributes.Name;
import java.util.jar.JarFile;

public final class SpringCloudVersion {

    private SpringCloudVersion() {}

    public static String getVersion() {
        return determineSpringCloudVersion();
    }

    private static String determineSpringCloudVersion() {
        Class<?> bootstrapClass;
        try {
            bootstrapClass = Class.forName("org.springframework.cloud.bootstrap.BootstrapConfiguration");
        } catch (ClassNotFoundException e) {
            return "NOT IN SPRING CLOUD";
        }

        String implementationVersion = bootstrapClass.getPackage().getImplementationVersion();
        if (implementationVersion != null) {
            return implementationVersion;
        }
        CodeSource codeSource = bootstrapClass.getProtectionDomain().getCodeSource();
        if (codeSource == null) {
            return null;
        }
        URL codeSourceLocation = codeSource.getLocation();
        try {
            URLConnection connection = codeSourceLocation.openConnection();
            if (connection instanceof JarURLConnection jarConn) {
                return getImplementationVersion(jarConn.getJarFile());
            }
            try (JarFile jarFile = new JarFile(new File(codeSourceLocation.toURI()))) {
                return getImplementationVersion(jarFile);
            }
        } catch (Exception ex) {
            return null;
        }
    }

    private static String getImplementationVersion(JarFile jarFile) throws IOException {
        return jarFile.getManifest().getMainAttributes().getValue(Name.IMPLEMENTATION_VERSION);
    }
}
