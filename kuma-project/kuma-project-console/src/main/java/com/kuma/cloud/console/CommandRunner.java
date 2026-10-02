package com.kuma.cloud.console;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/** 固定参数调用本机命令；独立读取两条流，限制输出与执行时间。 */
@Component
public class CommandRunner {
    private static final int OUTPUT_LIMIT = 4 * 1024 * 1024;

    public record Result(boolean success, String output, String error) {}

    public Result run(List<String> command, Duration timeout) {
        Process process = null;
        try (var readers = Executors.newVirtualThreadPerTaskExecutor()) {
            process = new ProcessBuilder(command).start();
            Process running = process;
            var stdout = readers.submit(() -> running.getInputStream().readNBytes(OUTPUT_LIMIT + 1));
            var stderr = readers.submit(() -> running.getErrorStream().readNBytes(OUTPUT_LIMIT + 1));
            if (!process.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS)) {
                terminate(process);
                return new Result(false, "", "命令执行超时（" + timeout.toSeconds() + " 秒）");
            }
            byte[] output = stdout.get(2, TimeUnit.SECONDS);
            byte[] error = stderr.get(2, TimeUnit.SECONDS);
            if (output.length > OUTPUT_LIMIT || error.length > OUTPUT_LIMIT) {
                return new Result(false, "", "命令输出超过 4 MiB，请缩小查询范围");
            }
            return new Result(process.exitValue() == 0, decode(output).strip(), decode(error).strip());
        } catch (Exception e) {
            if (process != null) terminate(process);
            return new Result(false, "", e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
        }
    }

    static String decode(byte[] bytes) {
        // wsl --list uses UTF-16LE on Windows; Linux command stdout uses UTF-8.
        boolean utf16 = bytes.length > 1 && (bytes[0] == (byte) 0xff && bytes[1] == (byte) 0xfe);
        for (int i = 1; i < Math.min(bytes.length, 80); i += 2) utf16 |= bytes[i] == 0;
        return new String(bytes, utf16 ? StandardCharsets.UTF_16LE : StandardCharsets.UTF_8)
                .replace("\uFEFF", "").replace("\u0000", "");
    }

    private static void terminate(Process process) {
        process.descendants().forEach(ProcessHandle::destroyForcibly);
        process.destroyForcibly();
    }
}
