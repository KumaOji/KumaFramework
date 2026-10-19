package com.kuma.cloud.lab.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/** Real loopback TCP connections; connection-local state is intentionally reset on reconnect. */
public final class SocketLifecycleLesson {
    public record Step(String name, String explanation, Map<String, Object> evidence) {}
    public record Report(List<Step> steps, Map<String, Boolean> checks) {}
    private SocketLifecycleLesson() {}

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && !List.of("all", "lifecycle").contains(args[0])) throw new IllegalArgumentException("Choose all or lifecycle");
        System.out.println(args.length == 0 || args[0].equals("all") ? all() : run());
    }

    public static Report all() throws Exception {
        SocketLesson.run();
        var lifecycle = run();
        var checks = new LinkedHashMap<>(lifecycle.checks());
        checks.put("framingTimeoutAndUdp", true);
        return new Report(lifecycle.steps(), Map.copyOf(checks));
    }

    public static Report run() throws Exception {
        List<Step> steps = new ArrayList<>();
        Map<String, Boolean> checks = new LinkedHashMap<>();
        InetAddress loopback = InetAddress.getByName("127.0.0.1");
        var worker = Executors.newSingleThreadExecutor();
        try (ServerSocket listener = new ServerSocket(0, 10, loopback)) {
            listener.setSoTimeout(5000);
            var server = worker.submit(() -> {
                for (int connection = 1; connection <= 2; connection++) {
                    try (Socket peer = listener.accept()) {
                        peer.setSoTimeout(5000);
                        var input = new DataInputStream(peer.getInputStream());
                        var output = new DataOutputStream(peer.getOutputStream());
                        int sequence = 0;
                        // An empty UTF frame terminates the message loop; EOF is separately observed below.
                        String message;
                        while (!(message = input.readUTF()).isEmpty()) {
                            output.writeUTF(connection + ":" + (++sequence) + ":" + message);
                            output.flush();
                        }
                        if (input.read() != -1) throw new IllegalStateException("Expected client half-close EOF");
                        output.writeUTF("END:" + sequence);
                        output.flush();
                    }
                }
                return 2;
            });
            List<Integer> clientPorts = new ArrayList<>();
            for (int connection = 1; connection <= 2; connection++) {
                try (Socket client = new Socket()) {
                    check(checks, "initialState" + connection, !client.isConnected() && !client.isClosed());
                    client.connect(new InetSocketAddress(loopback, listener.getLocalPort()), 3000);
                    client.setSoTimeout(5000);
                    client.setTcpNoDelay(true);
                    clientPorts.add(client.getLocalPort());
                    steps.add(new Step("connect-" + connection, "观察客户端与服务端端点；重连使用新的 Socket。", Map.of(
                            "local", client.getLocalSocketAddress().toString(), "remote", client.getRemoteSocketAddress().toString(),
                            "connected", client.isConnected(), "tcpNoDelay", client.getTcpNoDelay())));
                    var input = new DataInputStream(client.getInputStream());
                    var output = new DataOutputStream(client.getOutputStream());
                    int messages = connection == 1 ? 3 : 1;
                    List<String> replies = new ArrayList<>();
                    for (int n = 1; n <= messages; n++) {
                        String message = "消息" + n;
                        output.writeUTF(message);
                        output.flush();
                        String reply = input.readUTF();
                        replies.add(reply);
                        check(checks, "reply" + connection + "_" + n, reply.equals(connection + ":" + n + ":" + message));
                    }
                    steps.add(new Step("exchange-" + connection, "同一长连接可连续收发；服务端连接内计数在重连后从1开始。", Map.of("replies", replies)));
                    output.writeUTF("");
                    output.flush();
                    client.shutdownOutput();
                    String finalReply = input.readUTF();
                    check(checks, "halfClose" + connection, client.isOutputShutdown() && !client.isClosed()
                            && finalReply.equals("END:" + messages) && input.read() == -1);
                    steps.add(new Step("half-close-" + connection, "shutdownOutput 只关闭发送方向，仍能接收最终回复和服务端 EOF。", Map.of("finalReply", finalReply, "outputShutdown", true)));
                    client.close();
                    check(checks, "closedState" + connection, client.isClosed() && client.isConnected());
                    boolean rejected = false;
                    try { client.connect(new InetSocketAddress(loopback, listener.getLocalPort()), 1000); }
                    catch (SocketException expected) { rejected = true; }
                    check(checks, "closedSocketCannotReconnect" + connection, rejected);
                    steps.add(new Step("closed-" + connection, "关闭后 isConnected 仍保留曾经连接的状态；不能据此判断存活，也不能复用已关闭 Socket。", Map.of("isClosed", client.isClosed(), "isConnected", client.isConnected(), "reconnectRejected", rejected)));
                }
            }
            check(checks, "twoConnectionsAccepted", server.get(10, TimeUnit.SECONDS) == 2);
            check(checks, "newClientPort", !clientPorts.get(0).equals(clientPorts.get(1)));
            return new Report(List.copyOf(steps), Map.copyOf(checks));
        } finally {
            worker.shutdownNow();
            if (!worker.awaitTermination(10, TimeUnit.SECONDS)) throw new IllegalStateException("Socket server thread did not stop");
        }
    }

    private static void check(Map<String, Boolean> checks, String name, boolean passed) {
        checks.put(name, passed);
        if (!passed) throw new IllegalStateException("Socket check failed: " + name);
    }
}
