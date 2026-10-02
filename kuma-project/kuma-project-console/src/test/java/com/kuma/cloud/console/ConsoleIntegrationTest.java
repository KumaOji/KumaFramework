package com.kuma.cloud.console;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsoleIntegrationTest {
    private ConsoleProperties properties(String url) {
        return new ConsoleProperties(false, "Ubuntu-24.04", "root",
                List.of(new ConsoleProperties.Project("Lab", url)));
    }

    @Test
    void labProxyPreservesBodyAuthAndUpstreamStatus() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/api/lab/test", exchange -> {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            String auth = exchange.getRequestHeaders().getFirst("Authorization");
            byte[] response = (exchange.getRequestMethod() + "|" + auth + "|" + body).getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(422, response.length);
            exchange.getResponseBody().write(response); exchange.close();
        });
        server.start();
        try {
            LabClient client = new LabClient(properties("http://127.0.0.1:" + server.getAddress().getPort() + "/api"));
            Map<String, Object> result = client.execute(new LabClient.Request("POST", "/lab/test",
                    "{\"message\":\"你好\"}", "Bearer test-only"));
            assertThat(result.get("status")).isEqualTo(422);
            assertThat(result.get("body")).isEqualTo("POST|Bearer test-only|{\"message\":\"你好\"}");
        } finally { server.stop(0); }
    }

    @Test
    void proxyCannotEscapeConfiguredLabOriginOrPath() {
        LabClient client = new LabClient(properties("http://127.0.0.1:9090/api"));
        for (String path : List.of("http://example.com", "//example.com/lab/test", "/lab/../../actuator/env",
                "/lab/%2e%2e/actuator/env", "/lab/\\evil", "/actuator/env")) {
            assertThatThrownBy(() -> client.target(path)).isInstanceOf(IllegalArgumentException.class);
        }
        assertThat(client.target("/lab/kafka/messages?direction=CONSUMED&limit=100").toString())
                .isEqualTo("http://127.0.0.1:9090/api/lab/kafka/messages?direction=CONSUMED&limit=100");
        assertThat(client.target("/lab/redis/string/demo?value=%E4%BD%A0%E5%A5%BD").getHost())
                .isEqualTo("127.0.0.1");
    }

    @Test
    void projectHealthDistinguishesReachableLoginFromHealthyService() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/api/actuator/health", exchange -> {
            exchange.getResponseHeaders().add("Location", "/login");
            exchange.sendResponseHeaders(302, -1); exchange.close();
        });
        server.start();
        try {
            ProjectMonitor monitor = new ProjectMonitor(properties("http://127.0.0.1:" + server.getAddress().getPort() + "/api"));
            monitor.sample();
            var entries = (List<?>) monitor.snapshot().get("projects");
            assertThat(((Map<?, ?>) entries.getFirst()).get("status")).isEqualTo("REACHABLE");
            assertThat(((Map<?, ?>) entries.getFirst()).get("httpStatus")).isEqualTo(302);
        } finally { server.stop(0); }
    }

    @Test
    void clusterDetailsRedactSecretsAndCredentialsButKeepDiagnostics() {
        ObjectMapper mapper = new ObjectMapper();
        ClusterMonitor monitor = new ClusterMonitor(new CommandRunner(), properties("http://127.0.0.1:9090/api"), mapper);
        String source = """
                {"items":[{"kind":"Secret","data":{"password":"sensitive"},"metadata":{"name":"db"}},
                  {"kind":"Pod","metadata":{"name":"mysql","managedFields":[{}]},"spec":{"containers":[
                    {"name":"mysql","env":[{"name":"MYSQL_ROOT_PASSWORD","value":"hidden"},
                       {"name":"LOG_LEVEL","value":"INFO"}]}]},"status":{"phase":"Running"}}]}
                """;
        String sanitized = mapper.writeValueAsString(monitor.sanitized(mapper.readTree(source)));
        assertThat(sanitized).doesNotContain("sensitive", "\"hidden\"", "managedFields")
                .contains("[已隐藏]", "Running", "INFO");
    }

    @Test
    void podNamesRejectFlagsAndShellCharacters() {
        for (String name : List.of("--all-namespaces", "pod;rm", "pod/name", "$(whoami)", ""))
            assertThatThrownBy(() -> ClusterMonitor.name(name)).isInstanceOf(IllegalArgumentException.class);
        ClusterMonitor.name("kafka-pool-0");
    }

    @Test
    void decodesBothWslDistributionListsAndUtf8LinuxOutput() {
        assertThat(CommandRunner.decode("Ubuntu-24.04\r\n".getBytes(StandardCharsets.UTF_16LE)))
                .isEqualTo("Ubuntu-24.04\r\n");
        assertThat(CommandRunner.decode("Linux 你好".getBytes(StandardCharsets.UTF_8))).isEqualTo("Linux 你好");
    }

    @Test
    void discoversApplicationMainClassesWithoutMistakingClasspathDependenciesForApps() {
        assertThat(ProjectDiscovery.identify("java.exe -classpath blog.jar;lab.jar org.gradle.launcher.daemon.bootstrap.GradleDaemon")).isNull();
        assertThat(ProjectDiscovery.identify("java.exe -cp many-jars com.kuma.cloud.blog.BlogApplication --spring.profiles.active=dev"))
                .isEqualTo("com.kuma.cloud.blog.BlogApplication");
        assertThat(ProjectDiscovery.identify("java -jar \"D:\\IDEA_project\\KumaFramework\\kuma-project\\kuma-project-lab\\build\\libs\\lab.jar\""))
                .isEqualTo("lab.jar");
        assertThat(ProjectDiscovery.identify("java -jar D:\\tools\\unrelated.jar")).isNull();
    }

    @Test
    void associatesListeningPortsWithPidsAndDeduplicatesIpv4Ipv6Bindings() {
        String text = """
                TCP    0.0.0.0:9000    0.0.0.0:0    LISTENING    123
                TCP    [::]:9000       [::]:0       LISTENING    123
                TCP    127.0.0.1:5005  0.0.0.0:0    LISTENING    123
                TCP    127.0.0.1:8000  127.0.0.1:9  ESTABLISHED  456
                """;
        var ports = ProjectDiscovery.listeningPorts(text);
        assertThat(ports.get(123L)).containsExactly(5005, 9000);
        assertThat(ports).doesNotContainKey(456L);
    }

    @Test
    void projectDiagnosticReaderRestrictsTargetsAndReadOnlyEndpoints() {
        ProjectDetails details = new ProjectDetails(properties("http://127.0.0.1:9090/api"));
        assertThatThrownBy(() -> details.read("Lab", "../env")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> details.read("Lab", "shutdown")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> details.read("Unknown", "health")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void catalogIncludesAllActiveProjectsAndNestedStarterPaths() throws Exception {
        var catalog = new ProjectCatalog(new ObjectMapper());
        var projects = (List<Map<String, Object>>) catalog.list().get("projects");
        assertThat(projects.stream().filter(p -> Boolean.TRUE.equals(p.get("enabled"))).map(p -> p.get("name")))
                .contains("kuma-project-project2", "kuma-project-project4", "kuma-project-blog", "kuma-project-uaa",
                        "kuma-project-gateway", "kuma-project-lab", "kuma-project-console");
        assertThat(projects.stream().filter(p -> !Boolean.TRUE.equals(p.get("enabled"))).map(p -> p.get("name")))
                .contains("kuma-project-project1", "kuma-project-project31");
        var starters = (List<Map<String, Object>>) catalog.dependencies(":kuma-project:kuma-project-blog").get("starters");
        var kafka = starters.stream().filter(s -> s.get("name").equals("kuma-boot-starter-mq-kafka")).findFirst().orElseThrow();
        assertThat(kafka.get("direct")).isEqualTo(false);
        assertThat((List<String>) kafka.get("path")).containsExactly(":kuma-project:kuma-project-blog",
                ":kuma-boot-framework:kuma-boot-starter-web", ":kuma-boot-framework:kuma-boot-starter-mq-kafka");
        assertThat(starters.stream().map(s -> s.get("name"))).doesNotContain("spring-boot-starter-test");
        assertThatThrownBy(() -> catalog.dependencies(":unknown")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void catalogDeduplicatesSharedAndCyclicStarterDependencies() {
        var mapper = new ObjectMapper();
        var source = mapper.readTree("""
                {"projects":[{"id":":app"}],"nodes":{
                  ":app":{"edges":[{"target":":a","scope":"api"},{"target":":b","scope":"implementation"}]},
                  ":a":{"name":"starter-a","starter":true,"edges":[{"target":":b","scope":"api"}]},
                  ":b":{"name":"starter-b","starter":true,"edges":[{"target":":a","scope":"api"}]}}}
                """);
        var dependencies = new ProjectCatalog(source).dependencies(":app");
        var starters = (List<Map<String, Object>>) dependencies.get("starters");
        assertThat(starters).hasSize(2);
        assertThat(starters).allSatisfy(s -> assertThat(s.get("direct")).isEqualTo(true));
    }

    @Test
    void catalogHonorsBranchExclusionsAndNonTransitiveEdges() {
        var source = new ObjectMapper().readTree("""
                {"projects":[{"id":":app"}],"nodes":{
                  ":app":{"edges":[{"target":":a","scope":"api","excludes":[{"module":"starter-common"}]},
                    {"target":":b","scope":"api"},{"target":":stop","scope":"api","transitive":false}]},
                  ":a":{"name":"starter-a","starter":true,"edges":[{"target":":common","scope":"api"}]},
                  ":b":{"name":"starter-b","starter":true,"edges":[{"target":":common","scope":"api"}]},
                  ":common":{"name":"starter-common","starter":true,"edges":[]},
                  ":stop":{"name":"starter-stop","starter":true,"edges":[{"target":":hidden","scope":"api"}]},
                  ":hidden":{"name":"starter-hidden","starter":true,"edges":[]}}}
                """);
        var starters = (List<Map<String, Object>>) new ProjectCatalog(source).dependencies(":app").get("starters");
        assertThat(starters.stream().map(s -> s.get("name"))).contains("starter-common", "starter-stop").doesNotContain("starter-hidden");
        var common = starters.stream().filter(s -> s.get("name").equals("starter-common")).findFirst().orElseThrow();
        assertThat((List<String>) common.get("path")).containsExactly(":app", ":b", ":common");
    }

    @Test
    void absenceOfLabDoesNotPreventMonitorStartup() {
        var client=new LabClient(new ConsoleProperties(false,"auto","root",List.of()));
        assertThatThrownBy(()->client.target("/lab/kafka/status")).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("未配置 Lab");
    }

    @Test
    void commandTimeoutTerminatesChildProcess() {
        String executable = System.getProperty("os.name").startsWith("Windows") ? "java.exe" : "java";
        String javaExecutable = java.nio.file.Path.of(System.getProperty("java.home"), "bin", executable).toString();
        var result = new CommandRunner().run(List.of(javaExecutable, "-version"), Duration.ZERO);
        assertThat(result.success()).isFalse();
        assertThat(result.error()).contains("超时");
    }
}
