package com.kuma.cloud.console;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.server.context.WebServerApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class BrowserLauncher {
    private static final Logger log = LoggerFactory.getLogger(BrowserLauncher.class);
    private final ConsoleProperties properties;

    public BrowserLauncher(ConsoleProperties properties) {
        this.properties = properties;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void open(ApplicationReadyEvent event) {
        if (!(event.getApplicationContext() instanceof WebServerApplicationContext context)) return;
        String url = "http://127.0.0.1:" + context.getWebServer().getPort();
        log.info("Kuma 本机控制台: {}", url);
        if (!properties.openBrowser()) return;
        try {
            String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
            ProcessBuilder launcher = os.contains("win")
                    ? new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", url)
                    : new ProcessBuilder(os.contains("mac") ? "open" : "xdg-open", url);
            if (os.contains("win")) {
                outer: for (String root : new String[]{System.getenv("ProgramFiles"), System.getenv("ProgramFiles(x86)"),
                        System.getenv("LOCALAPPDATA")}) {
                    if (root == null) continue;
                    for (String executable : new String[]{"Microsoft/Edge/Application/msedge.exe", "Google/Chrome/Application/chrome.exe"}) {
                        Path browser = Path.of(root).resolve(executable);
                        if (Files.isRegularFile(browser)) {
                            launcher = new ProcessBuilder(browser.toString(), "--app=" + url, "--new-window"); break outer;
                        }
                    }
                }
            }
            launcher.start();
        } catch (Exception e) {
            log.info("自动打开浏览器失败，请访问 {} ({})", url, e.getMessage());
        }
    }
}
