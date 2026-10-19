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
    private final RemoteServerMonitor remote;
    private final LabLifecycle lifecycle;
    private final BlogLifecycle blog;
    private final BlogBackend blogBackend;
    private final LocalComponents components;
    private final WslLifecycle wslLifecycle;

    public ConsoleController(HostMonitor host, ClusterMonitor cluster, ProjectMonitor projects, LabClient lab,
                             WslInspector wsl, ProjectDiscovery discovery, ProjectDetails projectDetails, ProjectCatalog catalog,
                             RemoteServerMonitor remote, LabLifecycle lifecycle, BlogLifecycle blog, BlogBackend blogBackend, LocalComponents components, WslLifecycle wslLifecycle) {
        this.host = host; this.cluster = cluster; this.projects = projects; this.lab = lab;
        this.wsl = wsl; this.discovery = discovery; this.projectDetails = projectDetails;
        this.catalog = catalog;
        this.remote = remote;
        this.lifecycle = lifecycle;
        this.blog = blog;
        this.blogBackend = blogBackend;
        this.components = components;
        this.wslLifecycle = wslLifecycle;
    }

    @GetMapping("/host") public Object host() { return host.snapshot(); }
    @GetMapping("/remote") public Object remote() { return remote.snapshot(); }
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
        checkOrigin(servlet);
        return lab.execute(request);
    }

    @GetMapping("/lab/process") public Object labProcess() { return lifecycle.status(); }
    @GetMapping("/lab/process/logs") public Object labLogs() throws java.io.IOException { return lifecycle.logs(); }
    @PostMapping("/lab/process/start") public Object startLab(HttpServletRequest servlet) {
        checkOrigin(servlet); return lifecycle.start();
    }
    @PostMapping("/lab/process/stop") public Object stopLab(HttpServletRequest servlet) {
        checkOrigin(servlet); return lifecycle.stop();
    }

    @GetMapping("/blog/process") public Object blogProcess() { return blog.status(); }
    @GetMapping("/blog/process/logs") public Object blogLogs() throws java.io.IOException { return blog.logs(); }
    @PostMapping("/blog/process/start") public Object startBlog(HttpServletRequest servlet) { checkOrigin(servlet); return blog.start(); }
    @PostMapping("/blog/process/stop") public Object stopBlog(HttpServletRequest servlet) { checkOrigin(servlet); return blog.stop(); }
    @PostMapping("/blog/process/sync") public Object syncBlogKnowledge(HttpServletRequest servlet) { checkOrigin(servlet); return blog.syncKnowledge(); }
    @GetMapping("/blog/backend") public Object blogBackend() { return blogBackend.status(); }
    @GetMapping("/blog/backend/logs") public Object blogBackendLogs() throws java.io.IOException { return blogBackend.logs(); }
    @PostMapping("/blog/backend/start") public Object startBlogBackend(HttpServletRequest servlet) { checkOrigin(servlet); return blogBackend.start(); }
    @PostMapping("/blog/backend/stop") public Object stopBlogBackend(HttpServletRequest servlet) { checkOrigin(servlet); return blogBackend.stop(); }
    @GetMapping("/components") public Object components() { return components.status(); }
    @GetMapping("/components/{name}/logs") public Object componentLogs(@PathVariable String name) throws java.io.IOException { return components.logs(name); }
    @PostMapping("/components/{name}/{action}") public Object componentAction(@PathVariable String name, @PathVariable String action, HttpServletRequest servlet) { checkOrigin(servlet); return components.action(name, action); }
    @GetMapping("/wsl/distributions") public Object wslDistributions() { return wslLifecycle.status(); }
    public record WslAction(String distribution) {}
    @PostMapping("/wsl/distributions/{action}") public Object wslAction(@PathVariable String action, @RequestBody WslAction body, HttpServletRequest servlet) {
        checkOrigin(servlet); return wslLifecycle.action(body.distribution(), action);
    }
    @PostMapping("/wsl/shutdown") public Object shutdownWsl(HttpServletRequest servlet) { checkOrigin(servlet); return wslLifecycle.shutdownAll(); }

    private void checkOrigin(HttpServletRequest servlet) {
        String origin = servlet.getHeader("Origin");
        if (origin != null) {
            URI uri = URI.create(origin);
            int port = uri.getPort() < 0 ? ("https".equals(uri.getScheme()) ? 443 : 80) : uri.getPort();
            if (!java.util.Set.of("127.0.0.1", "localhost", "[::1]").contains(uri.getHost())
                    || port != servlet.getLocalPort()) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> invalid(IllegalArgumentException e) { return Map.of("error", e.getMessage()); }
}
