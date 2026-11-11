package com.kuma.cloud.console;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.ObjectMapper;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConsoleShutdownTest {
    @TempDir Path root;

    private Process serviceProcess() throws Exception {
        return new ProcessBuilder("node", "-e", "setInterval(()=>{},1000)")
                .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                .redirectError(ProcessBuilder.Redirect.DISCARD).start();
    }

    private void closeConsoleWith(Object bean) {
        try (var context = new AnnotationConfigApplicationContext()) {
            context.registerBean("service", Object.class, () -> bean);
            context.refresh();
        }
    }

    @Test void closingConsolePreservesUaaGatewayAndBlogBackendIncludingLaunchers() throws Exception {
        var components = new LocalComponents(root.toString(), "Ubuntu-24.04", new CommandRunner(), new ObjectMapper());
        var blog = new BlogBackend(root.toString(), "Ubuntu-24.04", new CommandRunner(), new ObjectMapper());
        for (Object bean : List.of(components, blog)) {
            Process running = serviceProcess();
            Process launching = serviceProcess();
            try {
                List<?> services = bean == components
                        ? List.of(ReflectionTestUtils.getField(components, "uaa"), ReflectionTestUtils.getField(components, "gateway"))
                        : List.of(blog);
                for (Object service : services) {
                    ReflectionTestUtils.setField(service, "server", running.toHandle());
                    ReflectionTestUtils.setField(service, "launcher", launching);
                    ReflectionTestUtils.setField(service, "owned", true);
                }
                closeConsoleWith(bean);
                assertThat(running.isAlive()).as("project service survives Console shutdown").isTrue();
                assertThat(launching.isAlive()).as("in-progress launch survives Console shutdown").isTrue();
            } finally { LabLifecycle.terminate(launching); LabLifecycle.terminate(running); }
        }
    }

    @Test void closingConsolePreservesBlogFrontendAndKnowledgeBuild() throws Exception {
        var blog = new BlogLifecycle(root.toString(), root.toString(), 4321, new CommandRunner(), new ObjectMapper());
        Process frontend = serviceProcess();
        Process knowledge = serviceProcess();
        try {
            ReflectionTestUtils.setField(blog, "process", frontend.toHandle());
            ReflectionTestUtils.setField(blog, "synchronizing", knowledge);
            ReflectionTestUtils.setField(blog, "owned", true);
            closeConsoleWith(blog);
            assertThat(frontend.isAlive()).isTrue();
            assertThat(knowledge.isAlive()).isTrue();
        } finally { LabLifecycle.terminate(knowledge); LabLifecycle.terminate(frontend); }
    }

    @Test void closingConsolePreservesLab() throws Exception {
        var lab = new LabLifecycle(new ConsoleProperties(false, "", "", List.of()), root.toString());
        Process running = serviceProcess();
        try {
            ReflectionTestUtils.setField(lab, "process", running);
            closeConsoleWith(lab);
            assertThat(running.isAlive()).isTrue();
        } finally { LabLifecycle.terminate(running); }
    }
}
