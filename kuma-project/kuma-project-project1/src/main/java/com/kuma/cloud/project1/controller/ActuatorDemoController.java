package com.kuma.cloud.project1.controller;

import com.kuma.boot.actuator.mbean.KmcManagedResource;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Actuator 功能演示 Controller
 *
 * <p>演示如何在业务代码中使用 KmcManagedResource（JMX MBean + REST 双路访问）：
 * <ul>
 *   <li>GET  /demo/counter         - 查看当前计数器值
 *   <li>POST /demo/counter/increment - 计数器 +1
 *   <li>POST /demo/counter/reset    - 计数器归零
 *   <li>PUT  /demo/name            - 修改 MBean name 属性
 * </ul>
 *
 * <p>Actuator 端点（需在 application.yml 开放）：
 * <ul>
 *   <li>GET  /actuator/kmc                  - 查看自定义状态
 *   <li>POST /actuator/kmc/{key}            - 动态修改 status / detail
 *   <li>GET  /actuator/kmchealth            - kmc 健康检查
 *   <li>GET  /actuator/kmcstartup           - 启动耗时统计
 *   <li>GET  /actuator/kmcrequestmappings   - 所有 URL 映射
 *   <li>GET  /actuator/health               - 标准健康检查（含 kmc-version）
 *   <li>GET  /actuator/info                 - 应用信息（含 kmc-version）
 * </ul>
 */
@RestController
@RequestMapping("/demo")
@RequiredArgsConstructor
public class ActuatorDemoController {

    private final KmcManagedResource kmcManagedResource;

    /** 查看当前计数器状态 */
    @GetMapping("/counter")
    public Map<String, Object> getCounter() {
        return Map.of(
                "name", kmcManagedResource.getName(),
                "counter", kmcManagedResource.getCounter()
        );
    }

    /** 计数器 +1（同步更新 JMX MBean） */
    @PostMapping("/counter/increment")
    public Map<String, Object> increment() {
        kmcManagedResource.incrementCounter();
        return Map.of(
                "name", kmcManagedResource.getName(),
                "counter", kmcManagedResource.getCounter()
        );
    }

    /** 计数器归零 */
    @PostMapping("/counter/reset")
    public Map<String, Object> reset() {
        kmcManagedResource.resetCounter();
        return Map.of(
                "name", kmcManagedResource.getName(),
                "counter", kmcManagedResource.getCounter()
        );
    }

    /** 修改 MBean name 属性 */
    @PutMapping("/name")
    public Map<String, Object> updateName(@RequestParam String name) {
        kmcManagedResource.setName(name);
        return Map.of("name", kmcManagedResource.getName());
    }
}
