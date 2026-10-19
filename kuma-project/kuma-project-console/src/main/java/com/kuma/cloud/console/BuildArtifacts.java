package com.kuma.cloud.console;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.util.*;

/** Reuse bootJar outputs until production sources or Gradle inputs change. */
public final class BuildArtifacts {
    private BuildArtifacts() {}
    private record Inputs(String fingerprint, long newest) {}

    public static Path findJar(Path root, String name) throws IOException {
        Path libs = root.resolve("kuma-project/kuma-project-" + name + "/build/libs");
        if (!Files.isDirectory(libs)) return null;
        try (var files = Files.list(libs)) {
            return files.filter(p -> p.getFileName().toString().startsWith(name + "-") && p.toString().endsWith(".jar")
                            && !p.toString().endsWith("-plain.jar") && !p.toString().endsWith("-sources.jar"))
                    .max(Comparator.comparingLong(p -> p.toFile().lastModified())).orElse(null);
        }
    }

    private static Path stamp(Path root, String name) { return root.resolve("build/startup-cache/" + name + ".properties"); }
    private static Inputs inputs(Path root, String name) throws IOException {
        var files = new ArrayList<Path>();
        Files.walkFileTree(root, new SimpleFileVisitor<>() {
            @Override public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                String segment = dir.getFileName().toString();
                if (Set.of(".git", ".gradle", ".idea", ".pnpm-store", "node_modules", "build", "logs", "dist", ".codex", ".agents", ".nacos-tmp", "kuma-project-unable", "kuma-fronted-console").contains(segment)) return FileVisitResult.SKIP_SUBTREE;
                String relative = root.relativize(dir).toString().replace('\\', '/');
                if (relative.startsWith("kuma-project/kuma-project-") && relative.split("/").length == 2
                        && !relative.equals("kuma-project/kuma-project-" + name)) return FileVisitResult.SKIP_SUBTREE;
                return FileVisitResult.CONTINUE;
            }
            @Override public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                String relative = root.relativize(file).toString().replace('\\', '/');
                String filename = file.getFileName().toString();
                if (relative.contains("/src/main/") || filename.endsWith(".gradle") || filename.endsWith(".gradle.kts")
                        || filename.equals("gradle.properties") || relative.startsWith("gradle/")) files.add(file);
                return FileVisitResult.CONTINUE;
            }
        });
        files.sort(Comparator.comparing(p -> root.relativize(p).toString()));
        MessageDigest digest = digest(); long newest = 0;
        for (Path file : files) {
            digest.update(root.relativize(file).toString().getBytes(StandardCharsets.UTF_8)); digest.update((byte)0);
            try (var stream = Files.newInputStream(file)) { byte[] buffer = new byte[65536]; int read; while ((read = stream.read(buffer)) != -1) digest.update(buffer, 0, read); }
            digest.update((byte)0); newest = Math.max(newest, Files.getLastModifiedTime(file).toMillis());
        }
        digest.update(System.getProperty("java.version").getBytes(StandardCharsets.UTF_8));
        return new Inputs(HexFormat.of().formatHex(digest.digest()), newest);
    }
    private static MessageDigest digest() { try { return MessageDigest.getInstance("SHA-256"); } catch (Exception e) { throw new IllegalStateException(e); } }
    private static String jarIdentity(Path jar) throws IOException { return jar.getFileName() + ":" + Files.size(jar) + ":" + Files.getLastModifiedTime(jar).toMillis(); }

    public static boolean needsBuild(Path root, String name) throws IOException {
        Path jar = findJar(root, name);
        if (jar == null) return true;
        Inputs inputs = inputs(root, name);
        Path stamp = stamp(root, name);
        if (Files.exists(stamp)) {
            var saved = new Properties(); try (var stream = Files.newInputStream(stamp)) { saved.load(stream); }
            if (inputs.fingerprint.equals(saved.getProperty("inputs")) && jarIdentity(jar).equals(saved.getProperty("jar"))) return false;
            // An external Gradle build may already have refreshed the artifact.
            if (inputs.newest > Files.getLastModifiedTime(jar).toMillis() || jarIdentity(jar).equals(saved.getProperty("jar"))) return true;
        } else if (inputs.newest > Files.getLastModifiedTime(jar).toMillis()) return true;
        save(root, name, inputs, jar);
        return false;
    }
    public static void record(Path root, String name) throws IOException {
        Path jar = findJar(root, name); if (jar == null) throw new IOException("构建后未找到 " + name + " JAR");
        save(root, name, inputs(root, name), jar);
    }
    private static void save(Path root, String name, Inputs inputs, Path jar) throws IOException {
        Path stamp = stamp(root, name); Files.createDirectories(stamp.getParent());
        var data = new Properties(); data.setProperty("inputs", inputs.fingerprint); data.setProperty("jar", jarIdentity(jar));
        try (var stream = Files.newOutputStream(stamp)) { data.store(stream, "startup artifact cache"); }
    }
    public static Path runtimeCopy(Path jar, Path directory, String name) throws IOException {
        var digest = digest();
        try (var stream = Files.newInputStream(jar)) { byte[] buffer = new byte[65536]; int read; while ((read = stream.read(buffer)) != -1) digest.update(buffer, 0, read); }
        Files.createDirectories(directory);
        Path cached = directory.resolve(name + "-" + HexFormat.of().formatHex(digest.digest()) + ".jar");
        if (!Files.exists(cached) || Files.size(cached) != Files.size(jar)) {
            Path temporary = directory.resolve(name + "-" + UUID.randomUUID() + ".tmp");
            Files.copy(jar, temporary);
            try { Files.move(temporary, cached, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING); }
            catch (AtomicMoveNotSupportedException e) { Files.move(temporary, cached, StandardCopyOption.REPLACE_EXISTING); }
        }
        return cached;
    }
}
