package com.kuma.cloud.console;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import static org.assertj.core.api.Assertions.*;

class BuildArtifactsTest {
    @TempDir Path root;
    private Path source(String module) throws Exception {
        Path file = root.resolve(module + "/src/main/java/Example.java"); Files.createDirectories(file.getParent()); Files.writeString(file, "class Example {}"); return file;
    }
    private Path jar(String name) throws Exception {
        Path jar = root.resolve("kuma-project/kuma-project-" + name + "/build/libs/" + name + "-test.jar"); Files.createDirectories(jar.getParent()); Files.writeString(jar, "artifact");
        Files.setLastModifiedTime(jar, FileTime.fromMillis(System.currentTimeMillis() + 10000)); return jar;
    }
    @Test void missingArtifactRequiresBuildAndExistingArtifactIsReused() throws Exception {
        assertThat(BuildArtifacts.needsBuild(root, "lab")).isTrue();
        source("kuma-project/kuma-project-lab"); jar("lab");
        assertThat(BuildArtifacts.needsBuild(root, "lab")).isFalse();
        assertThat(BuildArtifacts.needsBuild(root, "lab")).isFalse();
    }
    @Test void detectsChangedOrRemovedSourceEvenWhenItsTimestampIsPreserved() throws Exception {
        Path source = source("kuma-project/kuma-project-lab"); jar("lab");
        assertThat(BuildArtifacts.needsBuild(root, "lab")).isFalse();
        var timestamp = Files.getLastModifiedTime(source); Files.writeString(source, "class Changed {}"); Files.setLastModifiedTime(source, timestamp);
        assertThat(BuildArtifacts.needsBuild(root, "lab")).isTrue();
        BuildArtifacts.record(root, "lab"); assertThat(BuildArtifacts.needsBuild(root, "lab")).isFalse();
        Files.delete(source); assertThat(BuildArtifacts.needsBuild(root, "lab")).isTrue();
    }
    @Test void sharedStarterAndGradleChangesInvalidateButOtherApplicationsDoNot() throws Exception {
        Path shared = source("kuma-boot-framework/kuma-boot-starter-common");
        Path unrelated = source("kuma-project/kuma-project-blog");
        jar("uaa"); assertThat(BuildArtifacts.needsBuild(root, "uaa")).isFalse();
        Files.writeString(unrelated, "class BlogChanged {}"); assertThat(BuildArtifacts.needsBuild(root, "uaa")).isFalse();
        Files.writeString(shared, "class StarterChanged {}"); assertThat(BuildArtifacts.needsBuild(root, "uaa")).isTrue();
        BuildArtifacts.record(root, "uaa"); Files.writeString(root.resolve("build.gradle"), "// new dependency");
        assertThat(BuildArtifacts.needsBuild(root, "uaa")).isTrue();
    }
    @Test void runtimeCopyIsReusedAndNewArtifactsGetSeparateCopies() throws Exception {
        Path jar = jar("lab"), directory = root.resolve("runtime");
        Path first = BuildArtifacts.runtimeCopy(jar, directory, "lab");
        assertThat(BuildArtifacts.runtimeCopy(jar, directory, "lab")).isEqualTo(first);
        Files.writeString(jar, "new artifact");
        Path second = BuildArtifacts.runtimeCopy(jar, directory, "lab");
        assertThat(second).isNotEqualTo(first); assertThat(Files.readString(first)).isEqualTo("artifact");
    }
}
