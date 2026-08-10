package com.kuma.cloud.lab.jni.support;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * 负责加载 JNI 动态库，兼容 Gradle bootRun、IDE 直启和 jar 运行场景。
 */
public final class JniLibraryLoader {

    private static final String LIBRARY_BASE_NAME = "lab_math";

    private static volatile boolean loaded;
    private static volatile String loadedPath;

    private JniLibraryLoader() {
    }

    public static synchronized void load() {
        if (loaded) {
            return;
        }

        UnsatisfiedLinkError lastError = null;
        for (String candidate : candidates()) {
            try {
                System.load(candidate);
                loaded = true;
                loadedPath = candidate;
                return;
            } catch (UnsatisfiedLinkError error) {
                lastError = error;
            }
        }

        try {
            System.loadLibrary(LIBRARY_BASE_NAME);
            loaded = true;
            loadedPath = LIBRARY_BASE_NAME;
            return;
        } catch (UnsatisfiedLinkError error) {
            if (lastError == null) {
                lastError = error;
            }
        }

        throw new IllegalStateException(
                "Unable to load JNI library '" + LIBRARY_BASE_NAME
                        + "'. Run './gradlew :kuma-project:kuma-project-lab:compileNative' first.",
                lastError
        );
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static String loadedPath() {
        return loadedPath;
    }

    private static List<String> candidates() {
        String fileName = libraryFileName();
        List<String> paths = new ArrayList<>();

        String configured = System.getProperty("kuma.lab.jni.library-path");
        if (configured != null && !configured.isBlank()) {
            paths.add(Paths.get(configured, fileName).toString());
        }

        paths.add(Paths.get("build", "native", fileName).toString());
        paths.add(Paths.get("kuma-project", "kuma-project-lab", "build", "native", fileName).toString());
        extractFromClasspath(fileName).ifPresent(paths::add);
        return paths;
    }

    private static String libraryFileName() {
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (os.contains("win")) {
            return LIBRARY_BASE_NAME + ".dll";
        }
        if (os.contains("mac")) {
            return "lib" + LIBRARY_BASE_NAME + ".dylib";
        }
        return "lib" + LIBRARY_BASE_NAME + ".so";
    }

    private static Optional<String> extractFromClasspath(String fileName) {
        try (InputStream input = JniLibraryLoader.class.getResourceAsStream("/native/" + fileName)) {
            if (input == null) {
                return Optional.empty();
            }
            Path temp = Files.createTempFile(LIBRARY_BASE_NAME + "-", "-" + fileName);
            temp.toFile().deleteOnExit();
            Files.copy(input, temp, StandardCopyOption.REPLACE_EXISTING);
            return Optional.of(temp.toAbsolutePath().toString());
        } catch (IOException error) {
            return Optional.empty();
        }
    }

}
