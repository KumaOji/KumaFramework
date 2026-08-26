package com.kuma.cloud.lab.javacore.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 简易 TCP 回显服务，用于演示 Java Socket 通信。
 */
public final class SocketLabSupport implements AutoCloseable {

    private final int port;
    private final int timeoutMillis;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "javacore-socket-server");
        thread.setDaemon(true);
        return thread;
    });

    private ServerSocket serverSocket;

    public SocketLabSupport(int port, int timeoutMillis) {
        this.port = port;
        this.timeoutMillis = timeoutMillis;
    }

    public synchronized void ensureStarted() throws IOException {
        if (running.get()) {
            return;
        }
        serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress("127.0.0.1", port));
        running.set(true);
        executor.submit(this::acceptLoop);
    }

    public String sendAndReceive(String message) throws IOException {
        ensureStarted();
        try (Socket client = new Socket()) {
            client.connect(new InetSocketAddress("127.0.0.1", port), timeoutMillis);
            client.setSoTimeout(timeoutMillis);

            PrintWriter writer = new PrintWriter(client.getOutputStream(), true, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));

            writer.println(message);
            String response = reader.readLine();
            return response == null ? "" : response;
        }
    }

    public boolean isRunning() {
        return running.get();
    }

    public int port() {
        return port;
    }

    private void acceptLoop() {
        while (running.get()) {
            try (Socket socket = serverSocket.accept()) {
                socket.setSoTimeout(timeoutMillis);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);

                String line = reader.readLine();
                if (line != null) {
                    writer.println("ECHO: " + line);
                }
            } catch (IOException ex) {
                if (running.get()) {
                    // 服务关闭或客户端断开时忽略
                }
            }
        }
    }

    @Override
    public synchronized void close() {
        running.set(false);
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException ignored) {
                // ignore
            }
        }
        executor.shutdownNow();
    }

}
