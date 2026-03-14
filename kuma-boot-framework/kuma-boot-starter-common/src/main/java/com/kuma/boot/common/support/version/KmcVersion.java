/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.common.support.version;

import static cn.hutool.core.util.ObjectUtil.defaultIfBlank;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.CodeSource;
import java.util.Properties;
import java.util.jar.Attributes.Name;
import java.util.jar.JarFile;
import cn.hutool.core.util.StrUtil;

/**
 * Class that exposes the Spring Boot version. Fetches the
 * {@link Name#IMPLEMENTATION_VERSION Implementation-Version} manifest attribute from the jar file
 * via {@link Package#getImplementationVersion()}, falling back to locating the jar file that
 * contains this class and reading the {@code Implementation-Version} attribute from its manifest.
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
public final class KmcVersion {

    private static final String DEFAULT_VERSION = "2026.01";

    private KmcVersion() {
    }

    /**
     * Return the full version string of the present Spring Boot codebase, or {@code null} if it
     * cannot be determined.
     *
     * @return the version of Spring Boot or {@code null}
     * @see Package#getImplementationVersion()
     */
    public static String getVersion() {
        return defaultIfBlank(determineKmcVersion(), DEFAULT_VERSION);
    }

    private static String determineKmcVersion() {
        // 1. MANIFEST.MF Implementation-Version（JAR 运行模式）
        String implementationVersion = KmcVersion.class.getPackage().getImplementationVersion();
        if (implementationVersion != null) {
            return implementationVersion;
        }
        // 2. Gradle processResources 生成的资源文件（IDE / exploded 模式）
        String resourceVersion = readVersionFromResource();
        if (resourceVersion != null) {
            return resourceVersion;
        }
        // 3. 物理 JAR 文件的 MANIFEST.MF（兜底）
        CodeSource codeSource = KmcVersion.class.getProtectionDomain().getCodeSource();
        if (codeSource == null) {
            return null;
        }
        URL codeSourceLocation = codeSource.getLocation();
        try {
            URLConnection connection = codeSourceLocation.openConnection();
            if (connection instanceof JarURLConnection) {
                return getImplementationVersion(((JarURLConnection) connection).getJarFile());
            }
            try (JarFile jarFile = new JarFile(new File(codeSourceLocation.toURI()))) {
                return getImplementationVersion(jarFile);
            }
        }
        catch (Exception ex) {
            return null;
        }
    }

    private static String getImplementationVersion(JarFile jarFile) throws IOException {
        return jarFile.getManifest().getMainAttributes().getValue(Name.IMPLEMENTATION_VERSION);
    }

    /**
     * 读取 Gradle processResources 阶段写入的版本资源文件。
     * 文件路径：classpath:/kmc-version.properties，由构建时展开 {@code ${project.version}}。
     * 若文件不存在或内容未被展开（仍为 {@code ${...}} 占位符），返回 null。
     */
    private static String readVersionFromResource() {
        try (InputStream is = KmcVersion.class.getResourceAsStream("/kmc-version.properties")) {
            if (is == null) {
                return null;
            }
            Properties props = new Properties();
            props.load(is);
            String v = props.getProperty("kmc-boot.version");
            // 未展开的占位符（IDE 未执行 processResources）直接忽略
            if (v == null || v.isBlank() || v.startsWith("${")) {
                return null;
            }
            return v;
        } catch (IOException e) {
            return null;
        }
    }
}
