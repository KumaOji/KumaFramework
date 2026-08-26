package com.kuma.cloud.lab.javacore.service.impl;

import com.kuma.cloud.lab.javacore.domain.dto.FileReadDTO;
import com.kuma.cloud.lab.javacore.domain.dto.FileWriteDTO;
import com.kuma.cloud.lab.javacore.domain.dto.HashMapInspectDTO;
import com.kuma.cloud.lab.javacore.domain.dto.SocketSendDTO;
import com.kuma.cloud.lab.javacore.domain.vo.ClassLoaderDemoVO;
import com.kuma.cloud.lab.javacore.domain.vo.FileDemoVO;
import com.kuma.cloud.lab.javacore.domain.vo.FileEntryVO;
import com.kuma.cloud.lab.javacore.domain.vo.FileOperationResultVO;
import com.kuma.cloud.lab.javacore.domain.vo.HashMapInspectVO;
import com.kuma.cloud.lab.javacore.domain.vo.JavaCoreOperationStepVO;
import com.kuma.cloud.lab.javacore.domain.vo.JavaCoreScenarioVO;
import com.kuma.cloud.lab.javacore.domain.vo.MarkWordDemoVO;
import com.kuma.cloud.lab.javacore.domain.vo.MarkWordStateVO;
import com.kuma.cloud.lab.javacore.domain.vo.SocketDemoVO;
import com.kuma.cloud.lab.javacore.service.JavaCoreLabService;
import com.kuma.cloud.lab.javacore.support.ClassLoaderInspector;
import com.kuma.cloud.lab.javacore.support.FileLabSupport;
import com.kuma.cloud.lab.javacore.support.HashMapStructureInspector;
import com.kuma.cloud.lab.javacore.support.MarkWordInspector;
import com.kuma.cloud.lab.javacore.support.SocketLabSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JavaCoreLabServiceImpl implements JavaCoreLabService {

    private final SocketLabSupport socketLabSupport;
    private final FileLabSupport fileLabSupport;

    @Override
    public JavaCoreScenarioVO runScenario() {
        List<JavaCoreOperationStepVO> steps = new ArrayList<>();

        ClassLoaderDemoVO classLoader = demonstrateClassLoading();
        steps.add(new JavaCoreOperationStepVO(
                "ClassLoader",
                "类加载器层次",
                classLoader,
                "Bootstrap → Platform → App，双亲委派保证核心类安全"));

        MarkWordDemoVO markWord = demonstrateMarkWord();
        steps.add(new JavaCoreOperationStepVO(
                "MarkWord",
                "对象头观察",
                markWord,
                "JOL 打印对象头，hashCode 与 synchronized 会改变 Mark Word"));

        HashMapInspectVO hashMap = inspectHashMapCollision();
        steps.add(new JavaCoreOperationStepVO(
                "HashMap",
                "哈希桶结构",
                hashMap,
                "数组下标由 spread(hash) 与 table.length 决定"));

        SocketDemoVO socket = sendSocketMessage(new SocketSendDTO() {{
            setMessage("hello-javacore");
        }});
        steps.add(new JavaCoreOperationStepVO(
                "Socket",
                "TCP 回显",
                socket,
                "ServerSocket 监听 + Socket 客户端一发一收"));

        FileDemoVO file = demonstrateFileOperations();
        steps.add(new JavaCoreOperationStepVO(
                "File",
                "NIO 文件读写",
                file,
                "Files API 完成写入、读取、复制与目录列举"));

        return new JavaCoreScenarioVO(steps);
    }

    @Override
    public ClassLoaderDemoVO demonstrateClassLoading() {
        return new ClassLoaderDemoVO(
                ClassLoaderInspector.inspectHierarchy(),
                ClassLoaderInspector.loadingPhases());
    }

    @Override
    public MarkWordDemoVO demonstrateMarkWord() {
        List<MarkWordStateVO> states = List.of(
                MarkWordInspector.inspectFreshObject(),
                MarkWordInspector.inspectAfterHashCode(),
                MarkWordInspector.inspectBiasedLockCandidate(),
                MarkWordInspector.inspectInstanceWithFields());

        return new MarkWordDemoVO(
                MarkWordInspector.vmDetails(),
                states,
                MarkWordInspector.klassLayout(),
                List.of(
                        "对象头 = Mark Word + Klass Pointer（压缩指针时可能合并为 12 字节）",
                        "Mark Word 存储：哈希码、GC 分代年龄、锁状态（无锁/偏向/轻量/重量）",
                        "实际布局受 JVM 位数、压缩指针、对齐填充影响，以 JOL 输出为准"));
    }

    @Override
    public HashMapInspectVO inspectHashMap(HashMapInspectDTO dto) {
        return HashMapStructureInspector.inspect(dto.getKeys());
    }

    @Override
    public HashMapInspectVO inspectHashMapCollision() {
        return HashMapStructureInspector.inspectCollisionDemo();
    }

    @Override
    public SocketDemoVO sendSocketMessage(SocketSendDTO dto) {
        try {
            String response = socketLabSupport.sendAndReceive(dto.getMessage());
            return new SocketDemoVO(
                    socketLabSupport.port(),
                    socketLabSupport.isRunning(),
                    dto.getMessage(),
                    response,
                    socketNotes());
        } catch (IOException ex) {
            throw new IllegalStateException("Socket 通信失败: " + ex.getMessage(), ex);
        }
    }

    @Override
    public FileDemoVO demonstrateFileOperations() {
        try {
            List<FileOperationResultVO> operations = new ArrayList<>();
            operations.add(fileLabSupport.writeText("demo/hello.txt", "KumaFramework Java 文件实验\n", false));
            operations.add(fileLabSupport.readText("demo/hello.txt"));
            operations.add(fileLabSupport.copy("demo/hello.txt", "demo/hello-copy.txt"));
            List<FileEntryVO> entries = fileLabSupport.list("demo");

            return new FileDemoVO(
                    fileLabSupport.workspace().toString(),
                    operations,
                    entries,
                    fileLabSupport.ioNotes());
        } catch (IOException ex) {
            throw new IllegalStateException("文件实验失败: " + ex.getMessage(), ex);
        }
    }

    @Override
    public FileOperationResultVO writeFile(FileWriteDTO dto) {
        try {
            return fileLabSupport.writeText(dto.getRelativePath(), dto.getContent(), dto.isAppend());
        } catch (IOException ex) {
            throw new IllegalStateException("写入文件失败: " + ex.getMessage(), ex);
        }
    }

    @Override
    public FileOperationResultVO readFile(FileReadDTO dto) {
        try {
            return fileLabSupport.readText(dto.getRelativePath());
        } catch (IOException ex) {
            throw new IllegalStateException("读取文件失败: " + ex.getMessage(), ex);
        }
    }

    private static List<String> socketNotes() {
        return List.of(
                "TCP：面向连接，三次握手后全双工字节流",
                "ServerSocket.accept() 阻塞等待客户端连接",
                "生产环境常用 NIO（Selector）或 Netty 处理高并发",
                "注意设置 SO_TIMEOUT 与资源 try-with-resources 关闭");
    }

}
