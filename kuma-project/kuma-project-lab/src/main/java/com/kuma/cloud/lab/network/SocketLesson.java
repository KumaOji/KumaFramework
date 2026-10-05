package com.kuma.cloud.lab.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/** TCP is a byte stream; UDP retains datagram boundaries. All endpoints bind loopback port 0. */
public final class SocketLesson {
    private static final InetAddress LOOPBACK;
    private static final int MAX_FRAME = 64 * 1024;
    static {
        try { LOOPBACK = InetAddress.getByName("127.0.0.1"); }
        catch (IOException e) { throw new ExceptionInInitializerError(e); }
    }
    private SocketLesson() { }

    public static void main(String[] args) throws Exception { run(); }

    public static void run() throws Exception {
        tcpFrames();
        frameBoundaries();
        readTimeout();
        udpDatagrams();
    }

    private static void tcpFrames() throws Exception {
        var worker = Executors.newSingleThreadExecutor();
        try (ServerSocket listener = new ServerSocket(0, 10, LOOPBACK)) {
            listener.setSoTimeout(5000);
            var server = worker.submit(() -> {
                try (Socket accepted = listener.accept()) {
                    accepted.setSoTimeout(5000);
                    int frames = 0;
                    byte[] payload;
                    while ((payload = readFrame(accepted.getInputStream())) != null) {
                        accepted.getOutputStream().write(frame(payload));
                        accepted.getOutputStream().flush();
                        frames++;
                    }
                    return frames;
                }
            });
            try (Socket client = new Socket()) {
                client.connect(new InetSocketAddress(LOOPBACK, listener.getLocalPort()), 3000);
                client.setSoTimeout(5000);
                byte[][] messages = {"分段消息\n含换行\0".getBytes(StandardCharsets.UTF_8),
                        "second".getBytes(StandardCharsets.UTF_8), new byte[0]};
                byte[] split = frame(messages[0]);
                // Deliberately split the header and payload across writes; packet boundaries are not asserted.
                client.getOutputStream().write(split, 0, 2);
                client.getOutputStream().flush();
                client.getOutputStream().write(split, 2, 3);
                client.getOutputStream().write(split, 5, split.length - 5);
                ByteArrayOutputStream combined = new ByteArrayOutputStream();
                combined.write(frame(messages[1]));
                combined.write(frame(messages[2]));
                client.getOutputStream().write(combined.toByteArray()); // two frames in one write
                client.getOutputStream().flush();
                client.shutdownOutput(); // send EOF while retaining the receive direction
                for (byte[] expected : messages) {
                    NetworkLearningDemo.check(Arrays.equals(expected, readFrame(client.getInputStream())), "TCP 帧回显不一致");
                }
                NetworkLearningDemo.check(readFrame(client.getInputStream()) == null, "服务端关闭后应得到 EOF");
            }
            NetworkLearningDemo.check(server.get(10, TimeUnit.SECONDS) == 3, "服务端应解析三个消息");
            System.out.println("TCP：长度前缀、分段/连续消息、UTF-8/换行/零字节/空帧、半关闭与 EOF 通过。");
        } finally {
            worker.shutdownNow();
            NetworkLearningDemo.check(worker.awaitTermination(10, TimeUnit.SECONDS), "TCP 线程退出超时");
        }
    }

    /** 4-byte big-endian unsigned-range-checked length, then exactly length payload bytes. */
    private static byte[] frame(byte[] payload) throws IOException {
        if (payload.length > MAX_FRAME) throw new IOException("frame too large");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bytes);
        out.writeInt(payload.length);
        out.write(payload);
        return bytes.toByteArray();
    }

    private static byte[] readFrame(InputStream input) throws IOException {
        int first = input.read();
        if (first == -1) return null; // clean EOF only at a frame boundary
        DataInputStream data = new DataInputStream(input);
        int length = (first << 24) | (data.readUnsignedByte() << 16)
                | (data.readUnsignedByte() << 8) | data.readUnsignedByte();
        if (length < 0 || length > MAX_FRAME) throw new IOException("invalid frame length: " + length);
        byte[] payload = new byte[length];
        data.readFully(payload); // read() is allowed to return less than requested
        return payload;
    }

    private static void frameBoundaries() throws Exception {
        byte[] limit = new byte[MAX_FRAME];
        NetworkLearningDemo.check(Arrays.equals(limit, readFrame(new ByteArrayInputStream(frame(limit)))), "最大合法帧失败");
        reject(new byte[]{0, 0}, true);
        reject(new byte[]{0, 0, 0, 2, 1}, true);
        reject(new byte[]{-1, -1, -1, -1}, false);
        reject(new byte[]{0, 1, 0, 1}, false);
        System.out.println("TCP 协议边界：截断头/截断数据/负长度/超限长度拒绝通过。");
    }

    private static void reject(byte[] bytes, boolean truncated) throws IOException {
        try {
            readFrame(new ByteArrayInputStream(bytes));
        } catch (IOException expected) {
            NetworkLearningDemo.check(!truncated || expected instanceof EOFException, "截断应报告 EOFException");
            return;
        }
        throw new IllegalStateException("非法帧未被拒绝");
    }

    private static void readTimeout() throws Exception {
        var worker = Executors.newSingleThreadExecutor();
        CountDownLatch accepted = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        try (ServerSocket listener = new ServerSocket(0, 10, LOOPBACK)) {
            listener.setSoTimeout(5000);
            var server = worker.submit(() -> {
                try (Socket peer = listener.accept()) {
                    peer.setSoTimeout(5000);
                    accepted.countDown();
                    NetworkLearningDemo.check(release.await(5, TimeUnit.SECONDS), "超时实验未释放服务端");
                    peer.getOutputStream().write(42);
                }
                return null;
            });
            try (Socket client = new Socket()) {
                client.connect(new InetSocketAddress(LOOPBACK, listener.getLocalPort()), 3000);
                NetworkLearningDemo.check(accepted.await(5, TimeUnit.SECONDS), "服务端未接受连接");
                client.setSoTimeout(150);
                try {
                    client.getInputStream().read();
                    throw new IllegalStateException("等待无数据连接时应读取超时");
                } catch (SocketTimeoutException expected) {
                    // Read timeout does not close the socket: prove it by receiving after releasing the server.
                    release.countDown();
                    client.setSoTimeout(5000);
                    NetworkLearningDemo.check(client.getInputStream().read() == 42, "读取超时后连接应仍可用");
                }
            }
            server.get(10, TimeUnit.SECONDS);
            System.out.println("TCP：SO_TIMEOUT 读取超时、超时后连接仍可用检查通过。");
        } finally {
            release.countDown();
            worker.shutdownNow();
            NetworkLearningDemo.check(worker.awaitTermination(10, TimeUnit.SECONDS), "超时实验线程退出超时");
        }
    }

    private static void udpDatagrams() throws Exception {
        var worker = Executors.newSingleThreadExecutor();
        try (DatagramSocket server = new DatagramSocket(new InetSocketAddress(LOOPBACK, 0));
             DatagramSocket client = new DatagramSocket(new InetSocketAddress(LOOPBACK, 0))) {
            server.setSoTimeout(5000);
            client.setSoTimeout(5000);
            client.connect(LOOPBACK, server.getLocalPort()); // peer filtering, not a TCP-style handshake
            var echo = worker.submit(() -> {
                for (int i = 0; i < 2; i++) {
                    DatagramPacket packet = new DatagramPacket(new byte[2048], 2048);
                    server.receive(packet);
                    server.send(new DatagramPacket(packet.getData(), packet.getLength(), packet.getSocketAddress()));
                }
                return null;
            });
            byte[][] payloads = {"UDP 第一条\0".getBytes(StandardCharsets.UTF_8), "second datagram".getBytes(StandardCharsets.UTF_8)};
            for (byte[] payload : payloads) client.send(new DatagramPacket(payload, payload.length));
            // UDP ordering is not assumed, even in this local example.
            boolean[] seen = new boolean[2];
            for (int i = 0; i < 2; i++) {
                DatagramPacket reply = new DatagramPacket(new byte[2048], 2048);
                client.receive(reply);
                byte[] received = Arrays.copyOfRange(reply.getData(), reply.getOffset(), reply.getOffset() + reply.getLength());
                int index = Arrays.equals(received, payloads[0]) ? 0 : Arrays.equals(received, payloads[1]) ? 1 : -1;
                NetworkLearningDemo.check(index >= 0 && !seen[index], "UDP 内容错误或重复");
                seen[index] = true;
            }
            echo.get(10, TimeUnit.SECONDS);
            System.out.println("UDP：两个独立数据报回显、消息边界与内容检查通过；不证明公网可靠性。");
        } finally {
            worker.shutdownNow();
            NetworkLearningDemo.check(worker.awaitTermination(10, TimeUnit.SECONDS), "UDP 线程退出超时");
        }
    }
}
