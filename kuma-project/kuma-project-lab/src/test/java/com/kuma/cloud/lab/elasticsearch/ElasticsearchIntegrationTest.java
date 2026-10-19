package com.kuma.cloud.lab.elasticsearch;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.assertj.core.api.Assertions.assertThat;

@EnabledIfEnvironmentVariable(named = "KUMA_LAB_ES_URL", matches = ".+")
class ElasticsearchIntegrationTest {
    @ParameterizedTest
    @ValueSource(strings = {"all", "crud", "search", "aggregation"})
    void verifiesRealEvidenceAndIndexCleanup(String lesson) throws Exception {
        String endpoint = ElasticsearchLearningDemo.configuredEndpoint().replaceAll("/+$", "");
        try (var lab = new ElasticsearchLearningDemo(endpoint); var http = HttpClient.newHttpClient()) {
            var report = lab.run(lesson);
            assertThat(report.checks()).isNotEmpty().containsEntry("indexCleaned", true).containsEntry("invisibleBeforeRefresh", true);
            assertThat(report.checks().values()).containsOnly(true);
            assertThat(report.steps()).allSatisfy(step -> assertThat(step.path()).startsWith("/" + report.index()));
            if (lesson.equals("all") || lesson.equals("search")) assertThat(report.checks()).containsEntry("filteredAndHighlighted", true);
            if (lesson.equals("all") || lesson.equals("aggregation")) assertThat(report.checks()).containsEntry("priceStatistics", true);
            if (lesson.equals("all") || lesson.equals("crud")) assertThat(report.checks()).containsEntry("documentDeleted", true);
            var missing = http.send(HttpRequest.newBuilder(URI.create(endpoint + "/" + report.index()))
                    .timeout(Duration.ofSeconds(5)).method("HEAD", HttpRequest.BodyPublishers.noBody()).build(), HttpResponse.BodyHandlers.discarding());
            assertThat(missing.statusCode()).isEqualTo(404);
        }
    }
}
