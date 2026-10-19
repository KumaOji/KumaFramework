package com.kuma.cloud.console;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tools.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import static org.assertj.core.api.Assertions.*;

class BlogBackendTest {
    @TempDir Path root;
    @Test void recognizesOnlyRepositoryBlogBackendJars() {
        var backend = new BlogBackend(root.toString(), "Ubuntu-24.04", new CommandRunner(), new ObjectMapper());
        String runtime = root.resolve("logs/blog-local/runtime/hash.jar").toString();
        assertThat(backend.matches("java.exe", "java -jar \"" + runtime + "\" --server.port=9000")).isTrue();
        assertThat(backend.matches("java.exe", "java -jar \"" + root.resolve("kuma-project/kuma-project-blog/build/libs/blog.jar") + "\"")).isTrue();
        assertThat(backend.matches("java.exe", "java -jar D:/other/blog.jar")).isFalse();
        assertThat(backend.matches("node.exe", "node -jar \"" + runtime + "\"")).isFalse();
        assertThat(backend.matches("java.exe", "java -jar \"" + root.resolve("logs/uaa-local/runtime/hash.jar") + "\"")).isFalse();
    }
}
