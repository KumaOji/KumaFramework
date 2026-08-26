package com.kuma.cloud.lab.javacore.service;

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

public interface JavaCoreLabService {

    JavaCoreScenarioVO runScenario();

    ClassLoaderDemoVO demonstrateClassLoading();

    MarkWordDemoVO demonstrateMarkWord();

    HashMapInspectVO inspectHashMap(HashMapInspectDTO dto);

    HashMapInspectVO inspectHashMapCollision();

    SocketDemoVO sendSocketMessage(SocketSendDTO dto);

    FileDemoVO demonstrateFileOperations();

    FileOperationResultVO writeFile(FileWriteDTO dto);

    FileOperationResultVO readFile(FileReadDTO dto);

}
