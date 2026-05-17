package com.kuma.boot.core.version;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.CodeSource;
import java.util.jar.Attributes.Name;
import java.util.jar.JarFile;

public final class SpringCloudAlibabaVersion {

    private SpringCloudAlibabaVersion() {}

    public static String getVersion() {
        return determineSpringCloudAlibabaVersion();
    }

    private static String determineSpringCloudAlibabaVersion() {
        Class<?> aClass = null;
        try {
            aClass = Class.forName("com.alibaba.cloud.nacos.NacosServiceManager");
        } catch (ClassNotFoundException e) {
            try {
                aClass = Class.forName("com.alibaba.cloud.nacos.NacosConfigManager");
            } catch (ClassNotFoundException e1) {
                // not in Spring Cloud Alibaba environment
            }
        }

        if (aClass == null) {
            return "NOT IN SPRING CLOUD ALIBABA";
        }

        String implementationVersion = aClass.getPackage().getImplementationVersion();
        if (implementationVersion != null) {
            return implementationVersion;
        }
        CodeSource codeSource = aClass.getProtectionDomain().getCodeSource();
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
