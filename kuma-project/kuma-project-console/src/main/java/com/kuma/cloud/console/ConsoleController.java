package com.kuma.cloud.console;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ConsoleController {
    private final HostMonitor host;
    private final ClusterMonitor cluster;
    private final ProjectMonitor projects;
    private final LabClient lab;
    private final WslInspector wsl;
    private final ProjectDiscovery discovery;
    private final ProjectDetails projectDetails;
    private final ProjectCatalog catalog;

    public ConsoleController(HostMonitor host, ClusterMonitor cluster, ProjectMonitor projects, LabClient lab,
                             WslInspector wsl, ProjectDiscovery discovery, ProjectDetails projectDetails, ProjectCatalog catalog) {
        this.host = host; this.cluster = cluster; this.projects = projects; this.lab = lab;
        this.wsl = wsl; this.discovery = discovery; this.projectDetails = projectDetails;
        this.catalog = catalog;
    }

    @GetMapping("/host") public Object host() { return host.snapshot(); }
    @GetMapping("/identity") public Object identity() { return Map.of("application", "kuma-local-console"); }
    @GetMapping("/cluster") public Object cluster() { return cluster.snapshot(); }
    @GetMapping("/projects") public Object projects() { return projects.snapshot(); }
    @GetMapping("/projects/running") public Object runningProjects() { return discovery.runningProjects(); }
    @GetMapping("/projects/catalog") public Object projectCatalog() { return catalog.list(); }
    @GetMapping("/projects/dependencies") public Object dependencies(@RequestParam String project) { return catalog.dependencies(project); }
    @GetMapping("/projects/details") public Object projectDetails(@RequestParam String name,
            @RequestParam String endpoint) { return projectDetails.read(name, endpoint); }
    @GetMapping("/wsl") public Object wsl(@RequestParam String distribution) { return wsl.inspect(distribution); }
    @GetMapping("/cluster/resources") public Object resources(@RequestParam String type,
            @RequestParam(defaultValue = "") String namespace) { return cluster.resources(type, namespace); }
    @GetMapping("/cluster/logs") public Object logs(@RequestParam String namespace, @RequestParam String pod,
            @RequestParam(defaultValue = "") String container) { return cluster.logs(namespace, pod, container); }

    @PostMapping("/lab/request") public Object lab(@RequestBody LabClient.Request request, HttpServletRequest servlet) {
        String origin = servlet.getHeader("Origin");
        if (origin != null) {
            URI uri = URI.create(origin);
            int port = uri.getPort() < 0 ? ("https".equals(uri.getScheme()) ? 443 : 80) : uri.getPort();
            if (!java.util.Set.of("127.0.0.1", "localhost", "[::1]").contains(uri.getHost())
                    || port != servlet.getLocalPort()) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return lab.execute(request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> invalid(IllegalArgumentException e) { return Map.of("error", e.getMessage()); }
}
