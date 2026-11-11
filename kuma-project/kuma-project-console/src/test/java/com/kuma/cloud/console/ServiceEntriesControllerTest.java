package com.kuma.cloud.console;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

class ServiceEntriesControllerTest {
    @Test void centralDirectoryUsesConfiguredPortsAndKeepsGrafanaFirst() {
        var config = new MockEnvironment().withProperty("console.dsh.port", "3088")
                .withProperty("console.life.qinglong-port", "5701").withProperty("console.life.rsshub-port", "1201")
                .withProperty("console.blog.port", "4322").withProperty("console.panels.grafana-url", "http://localhost:3300/");
        var projects = List.of(new ConsoleProperties.Project("Gateway", "http://localhost:18080", "http://localhost:18081"));
        var life = new LifeLifecycle(new LifeLifecycleTest.Commands(), new tools.jackson.databind.ObjectMapper(), "Ubuntu-24.04", "D:/Vscode_project/life", 5701, 1201);
        var entries = new ServiceEntriesController(config, new ConsoleProperties(false, "auto", "root", projects), life).entries();
        assertThat(entries.getFirst().id()).isEqualTo("grafana");
        assertThat(entries.getFirst().url()).isEqualTo("http://localhost:3300/");
        assertThat(entries).extracting(ServiceEntriesController.Entry::id).doesNotHaveDuplicates();
        assertThat(entries).anySatisfy(e -> { assertThat(e.id()).isEqualTo("dsh"); assertThat(e.url()).isEqualTo("http://localhost:3088"); });
        assertThat(entries).anySatisfy(e -> { assertThat(e.id()).isEqualTo("qinglong"); assertThat(e.url()).isEqualTo("http://172.23.89.45:5701"); assertThat(e.externalOnly()).isTrue(); });
        assertThat(entries).anySatisfy(e -> { assertThat(e.id()).isEqualTo("rsshub"); assertThat(e.url()).isEqualTo("http://172.23.89.45:1201"); });
        assertThat(entries).anySatisfy(e -> { assertThat(e.id()).isEqualTo("blog-frontend"); assertThat(e.url()).isEqualTo("http://localhost:4322"); });
        assertThat(entries).anySatisfy(e -> { assertThat(e.id()).isEqualTo("management-gateway"); assertThat(e.url()).isEqualTo("http://localhost:18081/actuator"); });
        assertThat(entries).allSatisfy(e -> assertThat(e.url()).doesNotContain("token=", "password", "deepseek.api.key"));
        assertThat(entries).anySatisfy(e -> { assertThat(e.id()).isEqualTo("mysql"); assertThat(e.readOnly()).isTrue(); assertThat(e.url()).isEqualTo("mysql://mysql.base:3306"); });
    }
}
