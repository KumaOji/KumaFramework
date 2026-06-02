package com.kuma.cloud.blog.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.StandardOpenOption;
import java.nio.file.Path;

/**
 * 音乐流式播放处理器
 *
 * <p>核心优化：
 * <ul>
 *   <li>Range 请求使用 {@link FileChannel#position(long)} 直接定位，完全跳过顺序 skip/read</li>
 *   <li>{@link FileChannel#transferTo} 走操作系统 sendfile(2) 零拷贝路径</li>
 *   <li>ThreadLocal 64 KB 缓冲区消除 per-request byte[] 分配及 GC 压力</li>
 * </ul>
 */
@Slf4j
@Component
public class MusicStreamHandler {

    private static final int BUFFER_SIZE = 65536;
    private static final ThreadLocal<byte[]> THREAD_BUFFER =
            ThreadLocal.withInitial(() -> new byte[BUFFER_SIZE]);

    public ResponseEntity<StreamingResponseBody> handleStreamRequest(
            Resource resource, HttpServletRequest request) {
        try {
            if (resource == null || !resource.exists()) {
                return ResponseEntity.notFound().build();
            }
            long fileLength = resource.contentLength();
            if (fileLength <= 0) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            String contentType = getContentType(resource);
            String rangeHeader = request.getHeader(HttpHeaders.RANGE);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_TYPE, contentType);
            headers.set(HttpHeaders.ACCEPT_RANGES, "bytes");
            headers.set(HttpHeaders.CACHE_CONTROL, "public, max-age=3600");

            if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
                return handleRangeRequest(resource, rangeHeader, fileLength, headers);
            }
            headers.set(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileLength));
            StreamingResponseBody body = out -> transfer(resource, 0, fileLength, out);
            return ResponseEntity.ok().headers(headers).body(body);
        } catch (Exception e) {
            log.error("音乐流请求处理失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private ResponseEntity<StreamingResponseBody> handleRangeRequest(
            Resource resource, String rangeHeader, long fileLength, HttpHeaders headers) {
        String[] parts = rangeHeader.substring(6).split("-");
        long start = parts.length > 0 && !parts[0].isEmpty() ? Long.parseLong(parts[0]) : 0L;
        long end   = parts.length > 1 && !parts[1].isEmpty() ? Long.parseLong(parts[1]) : fileLength - 1;

        if (start > end || start < 0 || end >= fileLength) {
            return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
                    .header(HttpHeaders.CONTENT_RANGE, "bytes */" + fileLength)
                    .build();
        }

        long length = end - start + 1;
        headers.set(HttpHeaders.CONTENT_LENGTH, String.valueOf(length));
        headers.set(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + fileLength);

        final long s = start, l = length;
        StreamingResponseBody body = out -> transfer(resource, s, l, out);
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).headers(headers).body(body);
    }

    /**
     * 统一传输入口：file-backed 资源走 FileChannel 零拷贝，其余降级到流拷贝。
     */
    private void transfer(Resource resource, long offset, long length, OutputStream out) {
        try {
            if (resource.isFile()) {
                transferWithFileChannel(resource.getFile().toPath(), offset, length, out);
            } else {
                copyWithStream(resource.getInputStream(), offset, length, out);
            }
        } catch (IOException e) {
            if (!isClientDisconnect(e)) {
                log.warn("音乐流传输中断: {}", e.getMessage());
            }
        }
    }

    /**
     * FileChannel.transferTo — 在 Tomcat NIO 连接器下触发 OS sendfile(2) 零拷贝。
     * position() 直接定位，完全不需要顺序读过前段字节。
     */
    private static void transferWithFileChannel(Path path, long offset, long length,
                                                OutputStream out) throws IOException {
        WritableByteChannel dst = Channels.newChannel(out);
        try (FileChannel src = FileChannel.open(path, StandardOpenOption.READ)) {
            long pos = offset;
            long remaining = length;
            while (remaining > 0) {
                long n = src.transferTo(pos, remaining, dst);
                if (n <= 0) break;
                pos += n;
                remaining -= n;
            }
        }
        out.flush();
    }

    /**
     * 降级路径：InputStream + ThreadLocal 缓冲区，避免每次 new byte[]。
     */
    private static void copyWithStream(InputStream in, long offset, long length,
                                       OutputStream out) throws IOException {
        try (in) {
            // skip to offset — loop until fully skipped
            long toSkip = offset;
            while (toSkip > 0) {
                long skipped = in.skip(toSkip);
                if (skipped <= 0) break;
                toSkip -= skipped;
            }
            byte[] buf = THREAD_BUFFER.get();
            long remaining = length;
            int flushed = 0;
            while (remaining > 0) {
                int toRead = (int) Math.min(buf.length, remaining);
                int n = in.read(buf, 0, toRead);
                if (n == -1) break;
                out.write(buf, 0, n);
                remaining -= n;
                flushed += n;
                if (flushed >= 524288) { // flush every 512 KB
                    out.flush();
                    flushed = 0;
                }
            }
            out.flush();
        }
    }

    private static String getContentType(Resource resource) {
        String name = resource.getFilename();
        if (name == null) return "audio/mpeg";
        String lo = name.toLowerCase();
        if (lo.endsWith(".flac")) return "audio/flac";
        if (lo.endsWith(".mp3"))  return "audio/mpeg";
        if (lo.endsWith(".wav"))  return "audio/wav";
        if (lo.endsWith(".ogg"))  return "audio/ogg";
        if (lo.endsWith(".m4a"))  return "audio/mp4";
        if (lo.endsWith(".aac"))  return "audio/aac";
        if (lo.endsWith(".wma"))  return "audio/x-ms-wma";
        return "audio/mpeg";
    }

    private static boolean isClientDisconnect(IOException e) {
        for (Throwable t = e; t != null; t = t.getCause()) {
            String msg = t.getMessage();
            if (msg != null && isAbortMessage(msg)) return true;
        }
        return false;
    }

    private static boolean isAbortMessage(String msg) {
        return msg.contains("Connection reset by peer")
                || msg.contains("Response not usable after response errors")
                || msg.contains("Response not usable after async request completion")
                || msg.contains("I/O operation was interrupted")
                || msg.contains("XNIO000808");
    }
}
