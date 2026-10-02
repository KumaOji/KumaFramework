package com.kuma.cloud.console;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;

/** The build exports evaluated declarations, including dependencies expressed through version catalogs. */
@Service
public class ProjectCatalog {
    private final JsonNode catalog;

    @org.springframework.beans.factory.annotation.Autowired
    public ProjectCatalog(ObjectMapper mapper) throws IOException {
        try (var input = new ClassPathResource("project-catalog.json").getInputStream()) {
            catalog = mapper.readTree(input);
        }
    }

    ProjectCatalog(JsonNode catalog) { this.catalog = catalog; }

    public Map<String, Object> list() {
        List<Map<String, Object>> projects = new ArrayList<>();
        for (var project : catalog.path("projects")) {
            var deps = dependencies(project.path("id").asString());
            Map<String, Object> row = new LinkedHashMap<>();
            for (String key : List.of("id", "name", "directory", "description", "dependencySource", "jarName", "shortName"))
                row.put(key, project.path(key).asString(""));
            row.put("mainClasses", project.path("mainClasses"));
            row.put("enabled", project.path("enabled").asBoolean());
            row.put("starters", deps.get("starters"));
            projects.add(row);
        }
        return Map.of("generatedAt", catalog.path("generatedAt").asString(""), "projects", projects);
    }

    public Map<String, Object> dependencies(String projectId) {
        boolean known = false;
        for (var p : catalog.path("projects")) if (p.path("id").asString().equals(projectId)) known = true;
        if (!known) throw new IllegalArgumentException("未知项目：" + projectId);
        var graph = catalog.path("nodes");
        var direct = new HashSet<String>();
        for (var edge : graph.path(projectId).path("edges")) direct.add(edge.path("target").asString());
        record Step(String id, List<String> path, List<String> scopes, List<JsonNode> excludes, boolean expand) { }
        Queue<Step> pending = new ArrayDeque<>();
        pending.add(new Step(projectId, List.of(projectId), List.of(), List.of(), true));
        Set<String> visited = new LinkedHashSet<>();
        Set<String> expanded = new HashSet<>();
        List<Map<String, Object>> starters = new ArrayList<>();
        Map<String, JsonNode> nodes = new LinkedHashMap<>();
        List<String> missing = new ArrayList<>();
        while (!pending.isEmpty()) {
            Step step = pending.remove();
            var node = graph.path(step.id());
            if (excluded(step.id(), node, step.excludes())) continue;
            boolean first = visited.add(step.id());
            if (node.isMissingNode()) { missing.add(step.id()); continue; }
            nodes.put(step.id(), node);
            if (first && node.path("starter").asBoolean(false)) {
                starters.add(Map.of("id", step.id(), "name", node.path("name").asString(),
                        "external", node.path("external").asBoolean(false), "direct", direct.contains(step.id()),
                        "path", step.path(), "scopes", step.scopes()));
            }
            if (!step.expand() || !expanded.add(step.id() + step.excludes().toString())) continue;
            for (var edge : node.path("edges")) {
                var path = new ArrayList<>(step.path()); path.add(edge.path("target").asString());
                var scopes = new ArrayList<>(step.scopes()); scopes.add(edge.path("scope").asString());
                var excludes = new ArrayList<>(step.excludes());
                for (var rule : edge.path("excludes")) if (!excludes.contains(rule)) excludes.add(rule);
                pending.add(new Step(edge.path("target").asString(), path, scopes, excludes,
                        edge.path("transitive").asBoolean(true)));
            }
        }
        starters.sort(Comparator.comparing(s -> s.get("name").toString()));
        return Map.of("projectId", projectId, "starters", starters, "nodes", nodes, "missing", missing);
    }

    private static boolean excluded(String id, JsonNode node, List<JsonNode> rules) {
        String group = node.path("external").asBoolean(false) ? id.split(":")[0] : "io.github.kumaoji";
        String module = node.path("name").asString("");
        for (var rule : rules) {
            String g = rule.path("group").asString(""), m = rule.path("module").asString("");
            if ((g.isEmpty() || g.equals("*") || g.equals(group)) && (m.isEmpty() || m.equals("*") || m.equals(module))) return true;
        }
        return false;
    }
}
