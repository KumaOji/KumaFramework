package com.kuma.cloud.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/** Desktop-only parent pipe; no network shutdown endpoint is exposed. */
final class DesktopShutdown {
    private DesktopShutdown() {}

    static void await(InputStream input, Runnable shutdown) {
        try {
            var reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals("kuma-console:quit")) break;
            }
        } catch (IOException ignored) {
            // Losing the owning desktop process also ends this Console backend.
        }
        shutdown.run();
    }
}
