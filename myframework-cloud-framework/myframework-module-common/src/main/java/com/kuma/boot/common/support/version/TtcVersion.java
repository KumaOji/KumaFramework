/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.ObjectUtil
 */
package com.kuma.boot.common.support.version;

import cn.hutool.core.util.ObjectUtil;
import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.CodeSource;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

public final class KmcVersion {
    private static final String DEFAULT_VERSION = "2026.01";

    private KmcVersion() {
    }

    public static String getVersion() {
        return (String)ObjectUtil.defaultIfBlank((CharSequence)KmcVersion.determineKmcVersion(), (CharSequence)DEFAULT_VERSION);
    }

    private static String determineKmcVersion() {
        String string;
        String implementationVersion = KmcVersion.class.getPackage().getImplementationVersion();
        if (implementationVersion != null) {
            return implementationVersion;
        }
        CodeSource codeSource = KmcVersion.class.getProtectionDomain().getCodeSource();
        if (codeSource == null) {
            return null;
        }
        URL codeSourceLocation = codeSource.getLocation();
        URLConnection connection = codeSourceLocation.openConnection();
        if (connection instanceof JarURLConnection) {
            return KmcVersion.getImplementationVersion(((JarURLConnection)connection).getJarFile());
        }
        JarFile jarFile = new JarFile(new File(codeSourceLocation.toURI()));
        try {
            string = KmcVersion.getImplementationVersion(jarFile);
        }
        catch (Throwable throwable) {
            try {
                try {
                    jarFile.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (Exception ex) {
                return null;
            }
        }
        jarFile.close();
        return string;
    }

    private static String getImplementationVersion(JarFile jarFile) throws IOException {
        return jarFile.getManifest().getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION);
    }
}

