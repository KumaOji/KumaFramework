package com.kuma.cloud.lab.javacore.controller;

import com.kuma.boot.common.model.result.Result;
import com.kuma.cloud.lab.javacore.domain.dto.FileReadDTO;
import com.kuma.cloud.lab.javacore.domain.dto.FileWriteDTO;
import com.kuma.cloud.lab.javacore.domain.dto.HashMapInspectDTO;
import com.kuma.cloud.lab.javacore.domain.dto.SocketSendDTO;
import com.kuma.cloud.lab.javacore.domain.vo.ClassLoaderDemoVO;
import com.kuma.cloud.lab.javacore.domain.vo.FileDemoVO;
import com.kuma.cloud.lab.javacore.domain.vo.FileOperationResultVO;
import com.kuma.cloud.lab.javacore.domain.vo.HashMapInspectVO;
import com.kuma.cloud.lab.javacore.domain.vo.JavaCoreScenarioVO;
import com.kuma.cloud.lab.javacore.domain.vo.MarkWordDemoVO;
import com.kuma.cloud.lab.javacore.domain.vo.SocketDemoVO;
import com.kuma.cloud.lab.javacore.service.JavaCoreLabService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Java 基础知识学习")
@RestController
@RequestMapping("/lab/javacore")
@RequiredArgsConstructor
public class JavaCoreLabController {

    private final JavaCoreLabService javaCoreLabService;

    @Operation(summary = "综合场景：类加载 + Mark Word + HashMap + Socket + 文件处理")
    @PostMapping("/scenario")
    public Result<JavaCoreScenarioVO> scenario() {
        return Result.success(javaCoreLabService.runScenario());
    }

    @Operation(summary = "类加载器层次与加载阶段说明")
    @GetMapping("/classloader")
    public Result<ClassLoaderDemoVO> classLoader() {
        return Result.success(javaCoreLabService.demonstrateClassLoading());
    }

    @Operation(summary = "Mark Word / 对象头布局观察（基于 JOL）")
    @GetMapping("/markword")
    public Result<MarkWordDemoVO> markWord() {
        return Result.success(javaCoreLabService.demonstrateMarkWord());
    }

    @Operation(summary = "HashMap 内部桶位与扩容参数观察")
    @PostMapping("/hashmap/inspect")
    public Result<HashMapInspectVO> inspectHashMap(@Valid @RequestBody HashMapInspectDTO dto) {
        return Result.success(javaCoreLabService.inspectHashMap(dto));
    }

    @Operation(summary = "HashMap 哈希碰撞演示（相同桶索引的 key）")
    @GetMapping("/hashmap/collision")
    public Result<HashMapInspectVO> hashMapCollision() {
        return Result.success(javaCoreLabService.inspectHashMapCollision());
    }

    @Operation(summary = "Socket TCP 回显通信演示")
    @PostMapping("/socket/send")
    public Result<SocketDemoVO> socketSend(@Valid @RequestBody SocketSendDTO dto) {
        return Result.success(javaCoreLabService.sendSocketMessage(dto));
    }

    @Operation(summary = "文件处理综合演示（写入、读取、复制、列举）")
    @PostMapping("/file/scenario")
    public Result<FileDemoVO> fileScenario() {
        return Result.success(javaCoreLabService.demonstrateFileOperations());
    }

    @Operation(summary = "写入文本文件到实验工作目录")
    @PostMapping("/file/write")
    public Result<FileOperationResultVO> writeFile(@Valid @RequestBody FileWriteDTO dto) {
        return Result.success(javaCoreLabService.writeFile(dto));
    }

    @Operation(summary = "读取实验工作目录中的文本文件")
    @PostMapping("/file/read")
    public Result<FileOperationResultVO> readFile(@Valid @RequestBody FileReadDTO dto) {
        return Result.success(javaCoreLabService.readFile(dto));
    }

}
