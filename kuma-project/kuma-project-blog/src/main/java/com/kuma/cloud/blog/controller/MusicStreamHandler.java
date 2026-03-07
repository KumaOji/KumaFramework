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

/**
 * 音乐流式播放处理器
 * 处理 HTTP Range 请求、字节范围计算、流式传输等复杂逻辑
 */
@Slf4j
@Component
public class MusicStreamHandler {

    /**
     * 处理音乐流式播放请求（支持 Range）
     */
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
            } else {
                headers.set(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileLength));
                StreamingResponseBody body = outputStream -> copyStream(resource, outputStream);
                return ResponseEntity.ok().headers(headers).body(body);
            }
        } catch (Exception e) {
            log.error("Error handling music stream request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private ResponseEntity<StreamingResponseBody> handleRangeRequest(
            Resource resource, String rangeHeader, long fileLength, HttpHeaders headers) {
        String[] ranges = rangeHeader.substring(6).split("-");
        long start = 0;
        long end = fileLength - 1;

        if (ranges.length > 0 && !ranges[0].isEmpty()) {
            start = Long.parseLong(ranges[0]);
        }
        if (ranges.length > 1 && !ranges[1].isEmpty()) {
            end = Long.parseLong(ranges[1]);
        }

        if (start > end || start < 0 || end >= fileLength) {
            return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
                    .header(HttpHeaders.CONTENT_RANGE, "bytes */" + fileLength)
                    .build();
        }

        long contentLength = end - start + 1;
        headers.set(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength));
        headers.set(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + fileLength);

        Resource rangeResource = new RangeResource(resource, start, end);
        StreamingResponseBody body = outputStream -> copyStream(rangeResource, outputStream);

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).headers(headers).body(body);
    }

    /**
     * 复制流数据到输出流，支持断连重试和缓冲管理
     */
    private void copyStream(Resource resource, OutputStream outputStream) {
        byte[] buffer = new byte[8192];
        try (InputStream in = resource.getInputStream()) {
            int n;
            int written = 0;
            while ((n = in.read(buffer)) != -1) {
                try {
                    outputStream.write(buffer, 0, n);
                    written += n;
                    // 定期 flush 避免缓冲堆积，降低连接超时风险
                    if (written >= 65536) {
                        outputStream.flush();
                        written = 0;
                    }
                } catch (IOException e) {
                    if (!isClientDisconnect(e)) {
                        log.warn("流式写入中断: {}", e.getMessage());
                    }
                    return;
                }
            }
            outputStream.flush();
        } catch (IOException e) {
            if (!isClientDisconnect(e)) {
                log.warn("流式读取/写入异常: {}", e.getMessage());
            }
        }
    }

    private String getContentType(Resource resource) {
        String filename = resource.getFilename();
        if (filename == null) return "audio/mpeg";
        String lower = filename.toLowerCase();
        if (lower.endsWith(".flac")) return "audio/flac";
        if (lower.endsWith(".mp3")) return "audio/mpeg";
        if (lower.endsWith(".wav")) return "audio/wav";
        if (lower.endsWith(".ogg")) return "audio/ogg";
        if (lower.endsWith(".m4a")) return "audio/mp4";
        if (lower.endsWith(".aac")) return "audio/aac";
        if (lower.endsWith(".wma")) return "audio/x-ms-wma";
        return "audio/mpeg";
    }

    private static boolean isClientDisconnect(IOException e) {
        for (Throwable t = e; t != null; t = t.getCause()) {
            String msg = t.getMessage();
            if (msg != null && isStreamAbortMessage(msg)) return true;
        }
        return false;
    }

    private static boolean isStreamAbortMessage(String msg) {
        return msg.contains("Connection reset by peer")
                || msg.contains("Response not usable after response errors")
                || msg.contains("Response not usable after async request completion")
                || msg.contains("I/O operation was interrupted")
                || msg.contains("XNIO000808");
    }

    /**
     * Range 资源包装器：支持从指定字节位置读取指定范围的数据
     */
    private static class RangeResource implements Resource {
        private final Resource delegate;
        private final long start;
        private final long end;

        RangeResource(Resource delegate, long start, long end) {
            this.delegate = delegate;
            this.start = start;
            this.end = end;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            InputStream inputStream = delegate.getInputStream();
            long skipped = inputStream.skip(start);
            if (skipped < start) {
                long remaining = start - skipped;
                byte[] buffer = new byte[8192];
                while (remaining > 0) {
                    long toRead = Math.min(remaining, buffer.length);
                    long read = inputStream.read(buffer, 0, (int) toRead);
                    if (read == -1) break;
                    remaining -= read;
                }
            }
            return new RangeInputStream(inputStream, end - start + 1);
        }

        @Override
        public boolean exists() { return delegate.exists(); }

        @Override
        public boolean isReadable() { return delegate.isReadable(); }

        @Override
        public boolean isOpen() { return delegate.isOpen(); }

        @Override
        public boolean isFile() { return delegate.isFile(); }

        @Override
        public java.net.URL getURL() throws IOException { return delegate.getURL(); }

        @Override
        public java.net.URI getURI() throws IOException { return delegate.getURI(); }

        @Override
        public java.io.File getFile() throws IOException { return delegate.getFile(); }

        @Override
        public long contentLength() throws IOException { return end - start + 1; }

        @Override
        public long lastModified() throws IOException { return delegate.lastModified(); }

        @Override
        public Resource createRelative(String relativePath) throws IOException { return delegate.createRelative(relativePath); }

        @Override
        public String getFilename() { return delegate.getFilename(); }

        @Override
        public String getDescription() { return delegate.getDescription(); }
    }

    /**
     * Range 输入流：限制读取范围内的字节数
     */
    private static class RangeInputStream extends InputStream {
        private final InputStream delegate;
        private final long length;
        private long read = 0;

        RangeInputStream(InputStream delegate, long length) {
            this.delegate = delegate;
            this.length = length;
        }

        @Override
        public int read() throws IOException {
            if (read >= length) return -1;
            int result = delegate.read();
            if (result != -1) read++;
            return result;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (read >= length) return -1;
            long remaining = length - read;
            int toRead = (int) Math.min(len, remaining);
            int result = delegate.read(b, off, toRead);
            if (result != -1) read += result;
            return result;
        }

        @Override
        public void close() throws IOException {
            delegate.close();
        }
    }
}
