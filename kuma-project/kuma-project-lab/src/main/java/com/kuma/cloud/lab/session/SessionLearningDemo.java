package com.kuma.cloud.lab.session;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.catalina.session.StandardManager;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/** Real servlet sessions and HTTP cookies in an isolated, short-lived loopback Tomcat. */
public final class SessionLearningDemo {
    public record Step(String name, String explanation, String method, String path, int status,
                       List<String> setCookie, JsonNode response) {}
    public record Report(String lesson, List<Step> steps, Map<String, Boolean> checks) {}
    private static final String COOKIE = "KUMA_LAB_SESSION";
    private static final JsonMapper JSON = JsonMapper.builder().build();
    private SessionLearningDemo() {}

    public static void main(String[] args) throws Exception {
        System.out.println(JSON.writeValueAsString(run(args.length == 0 ? "all" : args[0])));
    }

    public static Report run(String lesson) throws Exception {
        if (!List.of("all", "lifecycle", "isolation", "expiry").contains(lesson)) {
            throw new IllegalArgumentException("Choose all, lifecycle, isolation or expiry");
        }
        var steps = new ArrayList<Step>();
        var checks = new LinkedHashMap<String, Boolean>();
        Path directory = Files.createTempDirectory("kuma-session-lab-");
        var tomcat = new Tomcat();
        Exception failure = null;
        try {
            tomcat.setBaseDir(directory.toString());
            tomcat.setPort(0);
            var connector = tomcat.getConnector();
            connector.setProperty("address", "127.0.0.1");
            connector.setProperty("maxThreads", "4");
            connector.setProperty("minSpareThreads", "1");
            var context = tomcat.addContext("", directory.toString());
            context.setSessionCookieName(COOKIE);
            context.setUseHttpOnly(true);
            context.setSessionTimeout(1);
            var processor = new Rfc6265CookieProcessor();
            processor.setSameSiteCookies("Lax");
            context.setCookieProcessor(processor);
            var manager = new StandardManager();
            manager.setPathname(null); // Do not persist demonstration sessions on shutdown.
            context.setManager(manager);
            Tomcat.addServlet(context, "lesson", new LessonServlet());
            context.addServletMappingDecoded("/*", "lesson");
            tomcat.start();
            URI endpoint = URI.create("http://127.0.0.1:" + connector.getLocalPort());
            var cookiesA = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
            var cookiesB = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
            try (var clientA = client(cookiesA); var clientB = client(cookiesB); var secondConnection = client(cookiesA);
                 var withoutCookies = client(null)) {
                if (lesson.equals("all") || lesson.equals("isolation")) isolation(endpoint, clientA, clientB, secondConnection, withoutCookies, steps, checks);
                if (lesson.equals("all") || lesson.equals("lifecycle")) lifecycle(endpoint, clientB, withoutCookies, steps, checks);
                if (lesson.equals("all") || lesson.equals("expiry")) expiry(endpoint, clientA, steps, checks);
            }
        } catch (Exception ex) {
            failure = ex;
            throw ex;
        } finally {
            try { tomcat.stop(); tomcat.destroy(); }
            catch (Exception cleanup) { if (failure != null) failure.addSuppressed(cleanup); else throw cleanup; }
            finally {
                try (var paths = Files.walk(directory)) {
                    for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) Files.deleteIfExists(path);
                }
            }
        }
        checks.put("containerAndFilesCleaned", true);
        return new Report(lesson, List.copyOf(steps), Map.copyOf(checks));
    }

    private static HttpClient client(CookieManager cookies) {
        var builder = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).version(HttpClient.Version.HTTP_1_1);
        if (cookies != null) builder.cookieHandler(cookies);
        return builder.build();
    }

    private static void isolation(URI endpoint, HttpClient a, HttpClient b, HttpClient secondConnection,
                                  HttpClient withoutCookies, List<Step> steps, Map<String, Boolean> checks) throws Exception {
        var empty = call(endpoint, a, "GET", "/inspect", null, "inspect", "getSession(false) 只查看，不创建 Session。", steps);
        check(checks, "inspectDoesNotCreate", !empty.path("exists").asBoolean() && steps.getLast().setCookie().isEmpty());
        var first = call(endpoint, a, "POST", "/visit", null, "client-a-create", "客户端A首次访问，服务端创建 Session 并设置 Cookie。", steps);
        String idA = first.path("id").asString();
        check(checks, "sessionCookieCreated", first.path("isNew").asBoolean() && first.path("visits").asInt() == 1
                && steps.getLast().setCookie().stream().anyMatch(cookie -> cookie.startsWith(COOKIE + "=") && cookie.contains("HttpOnly") && cookie.contains("SameSite=Lax")));
        var again = call(endpoint, a, "POST", "/visit", null, "client-a-repeat", "CookieManager 自动带回 Cookie，同一 Session 的计数变为2。", steps);
        check(checks, "cookieContinuesSession", again.path("id").asString().equals(idA) && again.path("visits").asInt() == 2 && !again.path("isNew").asBoolean());
        var other = call(endpoint, b, "POST", "/visit", null, "client-b", "独立 Cookie 容器代表另一个客户端，Session 和计数隔离。", steps);
        check(checks, "clientsIsolated", !other.path("id").asString().equals(idA) && other.path("visits").asInt() == 1);
        var differentSocket = call(endpoint, secondConnection, "POST", "/visit", null, "same-cookie-new-socket", "另一个 HttpClient 使用A的 Cookie 容器：TCP连接变化，Session仍继续。", steps);
        check(checks, "sessionSurvivesConnectionChange", differentSocket.path("id").asString().equals(idA)
                && differentSocket.path("visits").asInt() == 3 && differentSocket.path("remotePort").asInt() != again.path("remotePort").asInt());
        var noCookie1 = call(endpoint, withoutCookies, "POST", "/visit", null, "no-cookie-first", "无 CookieManager 的客户端不会保存 Set-Cookie。", steps);
        var noCookie2 = call(endpoint, withoutCookies, "POST", "/visit", null, "no-cookie-repeat", "再次访问时没有 Cookie，会得到新的 Session。", steps);
        check(checks, "missingCookieCreatesNewSession", !noCookie1.path("id").asString().equals(noCookie2.path("id").asString())
                && noCookie2.path("isNew").asBoolean() && noCookie2.path("visits").asInt() == 1);
    }

    private static void lifecycle(URI endpoint, HttpClient client, HttpClient replay, List<Step> steps, Map<String, Boolean> checks) throws Exception {
        var initial = call(endpoint, client, "POST", "/visit", null, "lifecycle-create", "建立用于生命周期实验的 Session。", steps);
        String oldId = initial.path("id").asString();
        var rotated = call(endpoint, client, "POST", "/rotate", null, "rotate-id", "changeSessionId 更换标识，保留服务端已有属性。", steps);
        check(checks, "rotationPreservesAttributes", !rotated.path("id").asString().equals(oldId)
                && rotated.path("visits").asInt() == initial.path("visits").asInt());
        var old = call(endpoint, replay, "GET", "/inspect", COOKIE + "=" + oldId, "replay-old-id", "单独发送旧 Cookie，验证旧 ID 已不能查到 Session。", steps);
        check(checks, "oldIdRejected", !old.path("exists").asBoolean() && !old.path("requestedSessionIdValid").asBoolean());
        var current = call(endpoint, client, "GET", "/inspect", null, "new-cookie", "自动接收新 Cookie 后仍能找到原来的属性。", steps);
        check(checks, "newCookieWorks", current.path("id").asString().equals(rotated.path("id").asString()));
        call(endpoint, client, "POST", "/logout", null, "invalidate", "invalidate 删除服务端 Session，并清除客户端 Cookie。", steps);
        var loggedOut = call(endpoint, client, "GET", "/inspect", null, "inspect-after-logout", "注销后的查看请求不创建新 Session。", steps);
        var invalidated = call(endpoint, replay, "GET", "/inspect", COOKIE + "=" + rotated.path("id").asString(), "replay-invalidated-id", "即使客户端重放注销前的 Cookie，服务端也不会恢复旧 Session。", steps);
        check(checks, "logoutInvalidatesServerSession", !loggedOut.path("exists").asBoolean() && !invalidated.path("exists").asBoolean());
        var recreated = call(endpoint, client, "POST", "/visit", null, "recreate", "注销后再次创建，得到新 ID 和全新的计数。", steps);
        check(checks, "newSessionAfterLogout", !recreated.path("id").asString().equals(rotated.path("id").asString()) && recreated.path("visits").asInt() == 1);
    }

    private static void expiry(URI endpoint, HttpClient client, List<Step> steps, Map<String, Boolean> checks) throws Exception {
        var expiring = call(endpoint, client, "POST", "/expire", null, "set-idle-timeout", "设置本次 Session 的空闲有效期为1秒；不修改全局容器配置。", steps);
        check(checks, "idleTimeoutConfigured", expiring.path("maxInactiveInterval").asInt() == 1);
        // No polling here: accessing a valid Session would refresh its idle timer.
        Thread.sleep(2100);
        var expired = call(endpoint, client, "GET", "/inspect", null, "inspect-expired", "保持无请求超过有效期后，容器拒绝过期 Session；Cookie可以仍留在客户端。", steps);
        check(checks, "idleSessionExpired", !expired.path("exists").asBoolean() && !expired.path("requestedSessionIdValid").asBoolean());
        var recreated = call(endpoint, client, "POST", "/visit", null, "create-after-expiry", "过期 Cookie 不会恢复服务端属性；重新创建得到新 ID。", steps);
        check(checks, "newSessionAfterExpiry", !recreated.path("id").asString().equals(expiring.path("id").asString()) && recreated.path("visits").asInt() == 1);
    }

    private static JsonNode call(URI endpoint, HttpClient client, String method, String path, String cookie,
                                 String name, String explanation, List<Step> steps) throws Exception {
        var request = HttpRequest.newBuilder(endpoint.resolve(path)).timeout(Duration.ofSeconds(5))
                .method(method, HttpRequest.BodyPublishers.noBody());
        if (cookie != null) request.header("Cookie", cookie);
        var response = client.send(request.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() != 200) throw new IOException("Session " + path + " returned " + response.statusCode() + ": " + response.body());
        var result = JSON.readTree(response.body());
        steps.add(new Step(name, explanation, method, path, response.statusCode(), response.headers().allValues("set-cookie"), result));
        return result;
    }

    private static void check(Map<String, Boolean> checks, String name, boolean passed) {
        checks.put(name, passed);
        if (!passed) throw new IllegalStateException("Session check failed: " + name);
    }

    private static final class LessonServlet extends HttpServlet {
        @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
            if (!"/inspect".equals(request.getPathInfo())) { response.sendError(404); return; }
            write(request, response, request.getSession(false));
        }
        @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
            String action = request.getPathInfo();
            if (!List.of("/visit", "/rotate", "/logout", "/expire").contains(action)) { response.sendError(404); return; }
            HttpSession session = request.getSession(true);
            switch (action) {
                case "/visit" -> { var count = (Integer) session.getAttribute("visits"); session.setAttribute("visits", count == null ? 1 : count + 1); }
                case "/rotate" -> request.changeSessionId();
                case "/expire" -> session.setMaxInactiveInterval(1);
                case "/logout" -> {
                    session.invalidate();
                    var cookie = new jakarta.servlet.http.Cookie(COOKIE, "");
                    cookie.setPath("/"); cookie.setMaxAge(0); cookie.setHttpOnly(true);
                    response.addCookie(cookie);
                    session = null;
                }
                default -> throw new IllegalStateException("Unsupported action");
            }
            write(request, response, session);
        }
        private void write(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
            var body = new LinkedHashMap<String, Object>();
            body.put("exists", session != null);
            body.put("id", session == null ? "" : session.getId());
            body.put("isNew", session != null && session.isNew());
            body.put("visits", session == null || session.getAttribute("visits") == null ? 0 : session.getAttribute("visits"));
            body.put("maxInactiveInterval", session == null ? 0 : session.getMaxInactiveInterval());
            body.put("requestedSessionIdValid", request.isRequestedSessionIdValid());
            body.put("remotePort", request.getRemotePort());
            body.put("serverPort", request.getLocalPort());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(JSON.writeValueAsString(body));
        }
    }
}
